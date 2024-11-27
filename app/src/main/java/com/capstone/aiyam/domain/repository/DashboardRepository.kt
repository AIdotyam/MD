package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getDailySummaries(year: Int, month: Int, week: Int): Flow<ResponseWrapper<List<DailySummary>>>
    fun getMonthlySummaries(year: Int): Flow<ResponseWrapper<List<MonthlySummary>>>
}
