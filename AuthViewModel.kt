package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val email: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val authService: AuthService) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(authService.getCurrentUserEmail())
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    init {
        _currentUserEmail.value = authService.getCurrentUserEmail()
    }

    fun checkUserStatus() {
        _currentUserEmail.value = authService.getCurrentUserEmail()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authService.login(email, password)
                .onSuccess { userEmail ->
                    _currentUserEmail.value = userEmail
                    _uiState.value = AuthUiState.Success(userEmail)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authService.register(email, password)
                .onSuccess { userEmail ->
                    _currentUserEmail.value = userEmail
                    _uiState.value = AuthUiState.Success(userEmail)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun logout() {
        authService.logout()
        _currentUserEmail.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    companion object {
        fun Factory(authService: AuthService): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(authService) as T
            }
        }
    }
}
