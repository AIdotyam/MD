package com.capstone.aiyam.presentation.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.domain.repository.NotificationPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationPreferencesRepository: NotificationPreferencesRepository
) : ViewModel() {
    fun getUser() = userRepository.getFirebaseUser()
    fun signOut() = userRepository.firebaseSignOut()

    fun savePushNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        notificationPreferencesRepository.savePushNotificationSetting(isActive)
    }}

    fun getPushNotificationSetting() = notificationPreferencesRepository.getPushNotificationSetting()

    fun saveEmailNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        notificationPreferencesRepository.saveEmailNotificationSetting(isActive)
    }}

    fun getEmailNotificationSetting() = notificationPreferencesRepository.getEmailNotificationSetting()
}
