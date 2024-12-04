package com.capstone.aiyam.domain.repository

import androidx.paging.PagingData
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Summary
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.capstone.aiyam.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getSummaries(): Flow<ResponseWrapper<List<Summary>>>
    fun getWeeklyAlerts(): Flow<PagingData<WeeklySummary>>
}
