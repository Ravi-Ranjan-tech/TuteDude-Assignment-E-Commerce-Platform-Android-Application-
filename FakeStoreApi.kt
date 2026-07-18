package com.example.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<FakeStoreProduct>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): FakeStoreProduct
}
