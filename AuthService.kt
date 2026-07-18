package com.example.data.service

interface AuthService {
    fun getCurrentUserEmail(): String?
    fun isUserLoggedIn(): Boolean
    suspend fun register(email: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<String>
    fun logout()
}
