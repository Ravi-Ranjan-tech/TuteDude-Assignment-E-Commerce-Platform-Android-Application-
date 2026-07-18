package com.example.data.service

import android.content.Context

object ServiceFactory {
    fun isFirebaseEnabled(context: Context): Boolean {
        return try {
            com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthService(context: Context): AuthService {
        return if (isFirebaseEnabled(context)) {
            FirebaseAuthService(context)
        } else {
            LocalFallbackAuthService(context)
        }
    }

    fun getProductService(context: Context): ProductService {
        return if (isFirebaseEnabled(context)) {
            FirebaseProductService(context)
        } else {
            LocalFallbackProductService(context)
        }
    }
}
