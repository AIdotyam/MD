package com.capstone.aiyam.data.local.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKey {
    val PUSH_NOTIFICATION_KEY = booleanPreferencesKey("push_notification_key")
    val EMAIL_NOTIFICATION_KEY = booleanPreferencesKey("email_notification_key")
    val PHONE_NUMBER_KEY = stringPreferencesKey("phone_number_key")
    val TELEGRAM_KEY = booleanPreferencesKey("telegram_number_key")
    val ON_BOARDING_KEY = booleanPreferencesKey("on_boarding_key")
}
