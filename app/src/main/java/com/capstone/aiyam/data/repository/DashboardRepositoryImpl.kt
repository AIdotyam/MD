package com.capstone.aiyam.data.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.remote.DashboardService
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.capstone.aiyam.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardService: DashboardService
) : DashboardRepository {
    override fun getDailySummaries(year: Int, month: Int, week: Int): Flow<ResponseWrapper<List<DailySummary>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val response = dashboardService.getDailySummaries(year, month, week)
            emit(ResponseWrapper.Success(response.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getMonthlySummaries(year: Int): Flow<ResponseWrapper<List<MonthlySummary>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val response = dashboardService.getMonthlySummaries(year)
            emit(ResponseWrapper.Success(response.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
