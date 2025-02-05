package com.umer.networklogger
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import umer.sheraz.shakelibrary.ApiLoggingInterceptor

object RetrofitInstance {
    private const val BASE_URL = "https://dummyjson.com/"  // Replace with your base URL


    fun getRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiLoggingInterceptor(context)) // Pass the context to ApiLoggingInterceptor
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Optional: Logs request/response
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    // Provide a way to access the ApiService
    fun getApiService(context: Context): ApiService {
        return getRetrofit(context).create(ApiService::class.java)
    }
}
