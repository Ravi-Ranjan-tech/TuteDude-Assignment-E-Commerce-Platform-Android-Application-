package com.example.data.service

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitTask(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                cont.resume(task.result)
            } else {
                cont.resumeWithException(task.exception ?: RuntimeException("Task failed"))
            }
        }
    }
}

class FirebaseAuthService(private val context: Context) : AuthService {
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth?.currentUser?.email
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth?.currentUser != null
    }

    override fun logout() {
        firebaseAuth?.signOut()
    }

    override suspend fun register(email: String, password: String): Result<String> {
        val auth = firebaseAuth ?: return Result.failure(IllegalStateException("Firebase is not initialized"))
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).awaitTask()
            Result.success(result.user?.email ?: "Registered Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        val auth = firebaseAuth ?: return Result.failure(IllegalStateException("Firebase is not initialized"))
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).awaitTask()
            Result.success(result.user?.email ?: "Logged In Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
