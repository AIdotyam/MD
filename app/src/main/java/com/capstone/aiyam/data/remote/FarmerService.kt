package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.CreateFarmerRequest
import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.UpdateNameRequest
import com.capstone.aiyam.domain.model.Farmer
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface FarmerService {

    @POST("farmers")
    suspend fun createFarmer(
//        @Header("Authorization") token: String,
        @Body request: CreateFarmerRequest
    ): DataWrapper<Farmer>

    @PUT("farmers/{uid}")
    suspend fun updateFarmer(
//        @Header("Authorization") token: String,
        @Body request: UpdateNameRequest
    ): DataWrapper<Farmer>
}
