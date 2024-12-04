package com.capstone.aiyam.data.repository

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.capstone.aiyam.data.local.preferences.PreferencesKey
import com.capstone.aiyam.data.local.preferences.emailPreferences
import com.capstone.aiyam.data.local.preferences.notificationPreferences
import com.capstone.aiyam.data.local.preferences.settingsPreferences
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsPreferencesRepositoryImpl @Inject constructor(
    private val context: Application
) : SettingsPreferencesRepository {
    override fun getPushNotificationSetting(): Flow<Boolean> {
        return context.notificationPreferences.data.map {
            it[PreferencesKey.PUSH_NOTIFICATION_KEY] ?: false
        }
    }

    override suspend fun savePushNotificationSetting(isActive: Boolean) {
        context.notificationPreferences.edit {
            it[PreferencesKey.PUSH_NOTIFICATION_KEY] = isActive
        }
    }

    override fun getEmailNotificationSetting(): Flow<Boolean> {
        return context.emailPreferences.data.map {
            it[PreferencesKey.EMAIL_NOTIFICATION_KEY] ?: false
        }
    }

    override suspend fun saveEmailNotificationSetting(isActive: Boolean) {
        context.emailPreferences.edit {
            it[PreferencesKey.EMAIL_NOTIFICATION_KEY] = isActive
        }
    }

    override fun getPhoneNumberSetting(): Flow<String> {
        return context.notificationPreferences.data.map {
            it[PreferencesKey.PHONE_NUMBER_KEY] ?: ""
        }
    }

    override suspend fun savePhoneNumberSetting(phoneNumber: String) {
        context.notificationPreferences.edit {
            it[PreferencesKey.PHONE_NUMBER_KEY] = phoneNumber
        }
    }
}
