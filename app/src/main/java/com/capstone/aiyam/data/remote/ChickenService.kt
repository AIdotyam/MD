package com.capstone.aiyam.data.remote

import com.capstone.aiyam.domain.model.Classification
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChickenService {

    @Multipart
    @POST("upload")
    suspend fun postChicken(
        @Part file: MultipartBody.Part
    ): Classification
}
