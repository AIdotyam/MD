package com.capstone.aiyam.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    fun getNotificationSetting(): Flow<Boolean>
    suspend fun saveNotificationSetting(isActive: Boolean)
}
