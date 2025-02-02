package com.umer.networklogger

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import umer.sheraz.shakelibrary.ApiLoggingInterceptor

object RetrofitInstance {

    private const val BASE_URL = "https://dummyjson.com/"  // Replace with your base URL
    val client = OkHttpClient.Builder()
        .addInterceptor(ApiLoggingInterceptor()) // Add your custom cURL logger
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Optional: Logs request/response
        })
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())  // To convert JSON to Kotlin objects
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
