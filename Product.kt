package com.example.data.model

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val images: List<String>,
    val uploaderName: String? = null,
    val uploaderContact: String? = null,
    val isRecommended: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
