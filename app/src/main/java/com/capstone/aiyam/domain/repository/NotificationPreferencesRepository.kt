package com.capstone.aiyam.domain.repository

import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
    fun getPushNotificationSetting(): Flow<Boolean>
    suspend fun savePushNotificationSetting(isActive: Boolean)
    fun getEmailNotificationSetting(): Flow<Boolean>
    suspend fun saveEmailNotificationSetting(isActive: Boolean)
}
