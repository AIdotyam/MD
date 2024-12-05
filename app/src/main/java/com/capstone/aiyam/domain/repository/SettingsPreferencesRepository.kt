package com.capstone.aiyam.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsPreferencesRepository {
    fun getPushNotificationSetting(): Flow<Boolean>
    suspend fun savePushNotificationSetting(isActive: Boolean)
    fun getEmailNotificationSetting(): Flow<Boolean>
    suspend fun saveEmailNotificationSetting(isActive: Boolean)
    fun getPhoneNumberSetting(): Flow<String>
    suspend fun savePhoneNumberSetting(phoneNumber: String)
    fun getTelegramNotificationSetting(): Flow<Boolean>
    suspend fun saveTelegramNotificationSetting(isActive: Boolean)
    fun getOnBoarding(): Flow<Boolean>
    suspend fun saveOnBoarding(isActive: Boolean)
}
