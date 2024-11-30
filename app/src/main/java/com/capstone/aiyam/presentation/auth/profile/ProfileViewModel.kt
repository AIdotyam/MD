package com.capstone.aiyam.presentation.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsPreferencesRepository: SettingsPreferencesRepository
) : ViewModel() {
    fun getUser() = userRepository.getFirebaseUser()
    fun signOut() = userRepository.firebaseSignOut()

    fun savePushNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        settingsPreferencesRepository.savePushNotificationSetting(isActive)
    }}

    fun getPushNotificationSetting() = settingsPreferencesRepository.getPushNotificationSetting()

    fun saveEmailNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        settingsPreferencesRepository.saveEmailNotificationSetting(isActive)
    }}

    fun getEmailNotificationSetting() = settingsPreferencesRepository.getEmailNotificationSetting()

    fun getPhoneNumberSetting() = settingsPreferencesRepository.getPhoneNumberSetting()
}
