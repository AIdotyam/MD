package com.capstone.aiyam.data.repository

import android.util.Log
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.remote.DashboardService
import com.capstone.aiyam.domain.model.Summary
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.capstone.aiyam.domain.repository.DashboardRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardService: DashboardService,
    private val userRepository: UserRepository
) : DashboardRepository {
    private val user = userRepository.getFirebaseUser()
    override fun getSummaries(): Flow<ResponseWrapper<List<Summary>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val summaries = withToken(user, userRepository::getFirebaseToken) {
                dashboardService.getSummaries(it)
            }
            emit(ResponseWrapper.Success(summaries.data.alertsSummary))
        } catch (e: IllegalAccessException) {
            emit(ResponseWrapper.Error("Unauthorized: ${e.message}"))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message ?: "Unknown error"))
        }
    }
}
