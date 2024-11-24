package com.capstone.aiyam.data.repository

import android.util.Log
import com.capstone.aiyam.data.dto.DataWrapper
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.UpdateAlertRequest
import com.capstone.aiyam.data.remote.AlertService
import com.capstone.aiyam.data.remote.ChickenService
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val alertService: AlertService
) : AlertRepository {
    override fun getAlerts(): Flow<ResponseWrapper<List<Alerts>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val response = alertService.getAlerts()
            Log.d("Alerts", response.toString())
            Log.d("Alerts", response.data.toString())
            emit(ResponseWrapper.Success(response.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getAlertById(id: Int): Flow<ResponseWrapper<Alerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val updateRequest = UpdateAlertRequest(isRead = true)
            val response = alertService.updateAlertById(id, updateRequest)
            emit(ResponseWrapper.Success(response.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
