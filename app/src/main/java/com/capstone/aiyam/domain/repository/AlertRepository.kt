package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Alerts
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getAlerts(): Flow<ResponseWrapper<List<Alerts>>>
    fun getAlertById(id: Int): Flow<ResponseWrapper<Alerts>>
}
