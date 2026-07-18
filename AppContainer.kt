package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.remote.FakeStoreApi
import com.example.data.repository.ProductRepository
import com.example.data.service.AuthService
import com.example.data.service.ServiceFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val authService: AuthService
    val productRepository: ProductRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val authService: AuthService by lazy {
        ServiceFactory.getAuthService(context)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val fakeStoreApi: FakeStoreApi by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FakeStoreApi::class.java)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepository(
            fakeStoreApi = fakeStoreApi,
            productService = ServiceFactory.getProductService(context),
            favoriteDao = database.favoriteDao()
        )
    }
}
