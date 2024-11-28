package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.domain.model.Classification
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChickenService {

    @Multipart
    @POST("upload")
    suspend fun postChicken(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): DataWrapper<Classification>

    @GET("histories")
    suspend fun getHistories(
        @Header("Authorization") token: String,
    ): DataWrapper<List<Classification>>
}
