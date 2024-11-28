package com.capstone.aiyam.data.repository

import android.app.Application
import androidx.datastore.preferences.core.edit
import com.capstone.aiyam.data.local.preferences.PreferencesKey
import com.capstone.aiyam.data.local.preferences.notificationPreferences
import com.capstone.aiyam.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationPreferencesRepositoryImpl @Inject constructor(
    private val context: Application
) : NotificationPreferencesRepository {
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
        return context.notificationPreferences.data.map {
            it[PreferencesKey.EMAIL_NOTIFICATION_KEY] ?: false
        }
    }

    override suspend fun saveEmailNotificationSetting(isActive: Boolean) {
        context.notificationPreferences.edit {
            it[PreferencesKey.EMAIL_NOTIFICATION_KEY] = isActive
        }
    }
}
