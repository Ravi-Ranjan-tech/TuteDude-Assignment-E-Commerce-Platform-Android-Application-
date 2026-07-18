package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteProduct
import com.example.data.model.Product
import com.example.data.remote.FakeStoreApi
import com.example.data.remote.FakeStoreProduct
import com.example.data.service.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val fakeStoreApi: FakeStoreApi,
    private val productService: ProductService,
    private val favoriteDao: FavoriteDao
) {
    // Convert FakeStoreProduct to standard Product model
    private fun mapToProduct(item: FakeStoreProduct): Product {
        return Product(
            id = "rec_${item.id}",
            title = item.title,
            description = item.description,
            price = item.price,
            category = item.category,
            images = listOf(item.image),
            uploaderName = "FakeStore Api",
            uploaderContact = "https://fakestoreapi.com",
            isRecommended = true
        )
    }

    // Convert FavoriteProduct (Room) to standard Product model
    private fun mapToProduct(item: FavoriteProduct): Product {
        return Product(
            id = item.id,
            title = item.title,
            description = item.description,
            price = item.price,
            category = item.category,
            images = item.getImagesList(),
            uploaderName = item.uploaderName,
            uploaderContact = item.uploaderContact,
            isRecommended = item.isRecommended,
            timestamp = item.timestamp
        )
    }

    // Convert Product to FavoriteProduct (Room)
    private fun mapToFavorite(item: Product): FavoriteProduct {
        return FavoriteProduct(
            id = item.id,
            title = item.title,
            description = item.description,
            price = item.price,
            category = item.category,
            imagesCsv = item.images.joinToString(","),
            uploaderName = item.uploaderName,
            uploaderContact = item.uploaderContact,
            isRecommended = item.isRecommended,
            timestamp = System.currentTimeMillis()
        )
    }

    // Fetch Recommended Products from Public API
    suspend fun getRecommendedProducts(): List<Product> {
        return try {
            fakeStoreApi.getProducts().map { mapToProduct(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Fetch User-Uploaded Products
    suspend fun getUploadedProducts(): List<Product> {
        val result = productService.getUploadedProducts()
        return result.getOrDefault(emptyList())
    }

    // Upload a new Product
    suspend fun uploadProduct(product: Product): Result<Unit> {
        return productService.uploadProduct(product)
    }

    // Favorites (Room Database interaction)
    fun getFavoriteProducts(): Flow<List<Product>> {
        return favoriteDao.getAllFavorites().map { list ->
            list.map { mapToProduct(it) }
        }
    }

    suspend fun addFavorite(product: Product) {
        favoriteDao.insertFavorite(mapToFavorite(product))
    }

    suspend fun removeFavorite(productId: String) {
        favoriteDao.deleteFavoriteById(productId)
    }

    fun isFavoriteFlow(productId: String): Flow<Boolean> {
        return favoriteDao.isFavoriteFlow(productId)
    }
}
