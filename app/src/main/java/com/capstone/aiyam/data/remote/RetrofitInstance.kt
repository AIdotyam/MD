package com.capstone.aiyam.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://my.api.mockaroo.com/" // Base URL Mockaroo

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Menggunakan Base URL
            .addConverterFactory(GsonConverterFactory.create()) // Konverter untuk JSON
            .build()
            .create(ApiService::class.java) // Buat instance dari ApiService
    }
}
