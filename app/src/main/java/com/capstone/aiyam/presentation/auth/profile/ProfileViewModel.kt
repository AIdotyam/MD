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

    fun saveNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        notificationPreferencesRepository.saveNotificationSetting(isActive)
    }}

    fun getNotificationSetting() = notificationPreferencesRepository.getNotificationSetting()
}
