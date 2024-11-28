package com.capstone.aiyam.data.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.data.dto.UpdateAlertRequest
import com.capstone.aiyam.data.remote.AlertService
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.repository.AlertRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.capstone.aiyam.utils.withToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val alertService: AlertService,
    private val userRepository: UserRepository
) : AlertRepository {
    private val user = userRepository.getFirebaseUser()

    override fun getAlerts(): Flow<ResponseWrapper<List<Alerts>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val alerts = withToken(
                user, userRepository::getFirebaseToken
            ) {
                alertService.getAlerts(it)
            }
            emit(ResponseWrapper.Success(alerts.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun getAlertById(id: Int): Flow<ResponseWrapper<Alerts>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val alert = withToken(
                user, userRepository::getFirebaseToken
            ) {
                val updateRequest = UpdateAlertRequest(isRead = true)
                alertService.updateAlertById(it, id, updateRequest)
            }
            emit(ResponseWrapper.Success(alert.data))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
