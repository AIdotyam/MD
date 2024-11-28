package com.capstone.aiyam.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferencesKey {
    val PUSH_NOTIFICATION_KEY = booleanPreferencesKey("push_notification_key")
    val EMAIL_NOTIFICATION_KEY = booleanPreferencesKey("push_notification_key")
}
