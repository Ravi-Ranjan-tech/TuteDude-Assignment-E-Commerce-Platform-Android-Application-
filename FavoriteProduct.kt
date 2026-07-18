package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteProduct(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imagesCsv: String, // Comma-separated list of image URLs
    val uploaderName: String? = null,
    val uploaderContact: String? = null,
    val isRecommended: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getImagesList(): List<String> {
        return if (imagesCsv.isBlank()) emptyList() else imagesCsv.split(",")
    }
}
