package com.example.data.service

import com.example.data.model.Product

interface ProductService {
    suspend fun uploadProduct(product: Product): Result<Unit>
    suspend fun getUploadedProducts(): Result<List<Product>>
}
