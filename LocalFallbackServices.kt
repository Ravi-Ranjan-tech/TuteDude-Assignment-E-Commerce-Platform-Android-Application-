package com.example.data.service

import android.content.Context
import com.example.data.model.Product
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class LocalFallbackAuthService(context: Context) : AuthService {
    private val prefs = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)

    override fun getCurrentUserEmail(): String? {
        return prefs.getString("current_user", null)
    }

    override fun isUserLoggedIn(): Boolean {
        return getCurrentUserEmail() != null
    }

    override fun logout() {
        prefs.edit().remove("current_user").apply()
    }

    override suspend fun register(email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        if (prefs.contains("user_$email")) {
            return Result.failure(IllegalArgumentException("Email is already registered"))
        }
        prefs.edit().putString("user_$email", password).apply()
        // Automatically login after registration
        prefs.edit().putString("current_user", email).apply()
        return Result.success(email)
    }

    override suspend fun login(email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        val savedPassword = prefs.getString("user_$email", null)
        return if (savedPassword == password) {
            prefs.edit().putString("current_user", email).apply()
            Result.success(email)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }
}

class LocalFallbackProductService(context: Context) : ProductService {
    private val prefs = context.getSharedPreferences("mock_product_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, Product::class.java)
    private val adapter = moshi.adapter<List<Product>>(listType)

    override suspend fun uploadProduct(product: Product): Result<Unit> {
        return try {
            val existing = getUploadedProducts().getOrDefault(emptyList()).toMutableList()
            existing.add(0, product)
            val json = adapter.toJson(existing)
            prefs.edit().putString("products_json", json).apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUploadedProducts(): Result<List<Product>> {
        return try {
            val json = prefs.getString("products_json", null)
            val products = if (!json.isNullOrEmpty()) {
                adapter.fromJson(json) ?: emptyList()
            } else {
                emptyList()
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
