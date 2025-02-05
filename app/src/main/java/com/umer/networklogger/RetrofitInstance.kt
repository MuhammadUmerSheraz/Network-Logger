package com.umer.networklogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import umer.sheraz.shakelibrary.ApiLoggingInterceptor

object RetrofitInstance {
    private const val BASE_URL = "https://dummyjson.com/"  // Replace with your base URL


    fun getRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiLoggingInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // Provide a way to access the ApiService
    fun getApiService(): ApiService {
        return getRetrofit().create(ApiService::class.java)
    }
}
