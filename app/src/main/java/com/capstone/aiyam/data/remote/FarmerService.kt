package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.CreateFarmerRequest
import com.capstone.aiyam.data.dto.TargetRequest
import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.GoogleRequest
import com.capstone.aiyam.data.dto.UpdateNameRequest
import com.capstone.aiyam.domain.model.Farmer
import com.capstone.aiyam.domain.model.TargetAlerts
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface FarmerService {
    @POST("farmers")
    suspend fun createFarmer(
        @Header("Authorization") token: String,
        @Body request: CreateFarmerRequest
    ): DataWrapper<Farmer>

    @PUT("farmers/{uid}")
    suspend fun updateFarmer(
        @Header("Authorization") token: String,
        @Body request: UpdateNameRequest
    ): DataWrapper<Farmer>

    @POST("auth/googles")
    suspend fun loginGoogle(
        @Body request: GoogleRequest,
    ): DataWrapper<GoogleRequest>

    @GET("target-alerts")
    suspend fun getTargetAlerts(
        @Header("Authorization") token: String,
    ): DataWrapper<TargetAlerts>

    @POST("target-alerts")
    suspend fun createTargetAlerts(
        @Header("Authorization") token: String,
        @Body request: TargetRequest // don't null
    ): DataWrapper<TargetAlerts>

    @PATCH("target-alerts")
    suspend fun updateTargetAlerts(
        @Header("Authorization") token: String,
        @Body request: TargetRequest // can null
    ): DataWrapper<TargetAlerts>
}
