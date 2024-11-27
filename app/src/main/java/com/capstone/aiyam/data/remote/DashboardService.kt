package com.capstone.aiyam.data.remote

import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardService {
    @GET("/api/summaries")
    fun getDailySummaries(
//        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("week") week: Int
    ): DataWrapper<List<DailySummary>>

    @GET("/api/summaries")
    fun getMonthlySummaries(
//        @Header("Authorization") token: String,
        @Query("year") year: Int,
    ): DataWrapper<List<MonthlySummary>>
}
