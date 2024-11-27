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
    override fun getNotificationSetting(): Flow<Boolean> {
        return context.notificationPreferences.data.map {
            it[PreferencesKey.NOTIFICATION_KEY] ?: false
        }
    }

    override suspend fun saveNotificationSetting(isActive: Boolean) {
        context.notificationPreferences.edit {
            it[PreferencesKey.NOTIFICATION_KEY] = isActive
        }
    }
}
