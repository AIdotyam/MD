package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.UpdateAlertRequest
import com.capstone.aiyam.domain.model.Alerts
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AlertService {
    @GET("notification")
    suspend fun getAlerts(
        @Header("Authorization") token: String,
    ): DataWrapper<List<Alerts>>

    @PATCH("notification/{id}")
    suspend fun updateAlertById(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateAlertRequest
    ): DataWrapper<Alerts>
}
