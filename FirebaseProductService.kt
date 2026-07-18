package com.example.data.service

import android.content.Context
import com.example.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseProductService(private val context: Context) : ProductService {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun uploadProduct(product: Product): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            val productMap = hashMapOf(
                "id" to product.id,
                "title" to product.title,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "images" to product.images,
                "uploaderName" to product.uploaderName,
                "uploaderContact" to product.uploaderContact,
                "isRecommended" to product.isRecommended,
                "timestamp" to product.timestamp
            )
            suspendCancellableCoroutine<Unit> { cont ->
                db.collection("products").document(product.id)
                    .set(productMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(Unit)
                        } else {
                            cont.resumeWithException(task.exception ?: RuntimeException("Firestore write failed"))
                        }
                    }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUploadedProducts(): Result<List<Product>> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized"))
        return try {
            val snapshot = suspendCancellableCoroutine { cont ->
                db.collection("products").orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(task.result)
                        } else {
                            cont.resumeWithException(task.exception ?: RuntimeException("Firestore read failed"))
                        }
                    }
            }
            val products = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.getString("id") ?: doc.id
                    val title = doc.getString("title") ?: ""
                    val description = doc.getString("description") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val category = doc.getString("category") ?: ""
                    val images = (doc.get("images") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    val uploaderName = doc.getString("uploaderName")
                    val uploaderContact = doc.getString("uploaderContact")
                    val isRecommended = doc.getBoolean("isRecommended") ?: false
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    Product(
                        id = id,
                        title = title,
                        description = description,
                        price = price,
                        category = category,
                        images = images,
                        uploaderName = uploaderName,
                        uploaderContact = uploaderContact,
                        isRecommended = isRecommended,
                        timestamp = timestamp
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
