package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.UpdateAlertRequest
import com.capstone.aiyam.domain.model.Alerts
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AlertService {

    @GET("notification")
    suspend fun getAlerts(): DataWrapper<List<Alerts>>

    @PATCH("notification/{id}")
    suspend fun updateAlertById(
        @Path("id") id: Int,
        @Body request: UpdateAlertRequest
    ): DataWrapper<Alerts>
}
