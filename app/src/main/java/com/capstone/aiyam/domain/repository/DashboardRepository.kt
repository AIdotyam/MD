package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Summary
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getSummaries(): Flow<ResponseWrapper<List<Summary>>>
}
