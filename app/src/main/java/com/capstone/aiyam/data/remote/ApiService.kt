package com.capstone.aiyam.data.remote

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("tes_ai_yam.json?key=6a874990") // Endpoint lengkap tanpa base URL
    fun getDashboardData(): Call<DashboardData>
}