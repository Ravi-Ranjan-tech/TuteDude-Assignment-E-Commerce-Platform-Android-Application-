package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Product
import com.example.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface UploadUiState {
    object Idle : UploadUiState
    object Loading : UploadUiState
    object Success : UploadUiState
    data class Error(val message: String) : UploadUiState
}

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _recommendedProducts = MutableStateFlow<List<Product>>(emptyList())
    val recommendedProducts: StateFlow<List<Product>> = _recommendedProducts.asStateFlow()

    private val _uploadedProducts = MutableStateFlow<List<Product>>(emptyList())
    val uploadedProducts: StateFlow<List<Product>> = _uploadedProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadUiState>(UploadUiState.Idle)
    val uploadState: StateFlow<UploadUiState> = _uploadState.asStateFlow()

    // Favorites stream directly from Room DB
    val favorites: StateFlow<List<Product>> = repository.getFavoriteProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        refreshAll()
    }

    fun refreshAll() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                // Fetch in parallel
                val recs = repository.getRecommendedProducts()
                val uploads = repository.getUploadedProducts()

                _recommendedProducts.value = recs
                _uploadedProducts.value = uploads
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load products: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    // Toggle Favorite
    fun toggleFavorite(product: Product, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                repository.removeFavorite(product.id)
            } else {
                repository.addFavorite(product)
            }
        }
    }

    fun isFavoriteFlow(productId: String): StateFlow<Boolean> {
        return repository.isFavoriteFlow(productId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )
    }

    // Upload new product
    fun uploadProduct(
        title: String,
        description: String,
        priceStr: String,
        category: String,
        images: List<String>,
        uploaderName: String,
        uploaderContact: String
    ) {
        val price = priceStr.toDoubleOrNull()
        if (title.isBlank() || description.isBlank() || price == null || price <= 0) {
            _uploadState.value = UploadUiState.Error("Please enter valid Title, Description, and Price")
            return
        }
        if (images.size < 3 || images.any { it.isBlank() }) {
            _uploadState.value = UploadUiState.Error("Please provide a minimum of 3 valid product image URLs")
            return
        }
        if (uploaderName.isBlank() || uploaderContact.isBlank()) {
            _uploadState.value = UploadUiState.Error("Please enter your Name and Contact details")
            return
        }

        _uploadState.value = UploadUiState.Loading

        viewModelScope.launch {
            val product = Product(
                id = "user_${UUID.randomUUID()}",
                title = title,
                description = description,
                price = price,
                category = category,
                images = images,
                uploaderName = uploaderName,
                uploaderContact = uploaderContact,
                isRecommended = false,
                timestamp = System.currentTimeMillis()
            )

            repository.uploadProduct(product)
                .onSuccess {
                    _uploadState.value = UploadUiState.Success
                    refreshAll() // Reload product list
                }
                .onFailure { error ->
                    _uploadState.value = UploadUiState.Error(error.localizedMessage ?: "Failed to upload product")
                }
        }
    }

    fun resetUploadState() {
        _uploadState.value = UploadUiState.Idle
    }

    companion object {
        fun Factory(repository: ProductRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductViewModel(repository) as T
            }
        }
    }
}
