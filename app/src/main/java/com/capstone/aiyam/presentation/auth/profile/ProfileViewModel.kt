package com.capstone.aiyam.presentation.auth.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsPreferencesRepository: SettingsPreferencesRepository
) : ViewModel() {
    fun getUser() = userRepository.getFirebaseUser()
    fun signOut() = userRepository.firebaseSignOut()

    init {
        initializeTarget().launchIn(viewModelScope)
    }

    private fun initializeTarget() = userRepository.getTargetAlerts()

    fun enableEmailAlerts(): Flow<ResponseWrapper<TargetAlerts>> = flow {
        when (val user = getUser()) {
            is AuthorizationResponse.Success -> {
                userRepository.updateEmailAlerts(user.user.email).collect { emit(it) }
            }

            is AuthorizationResponse.Error -> {
                emit(ResponseWrapper.Error(user.message))
            }
        }
    }

    fun disableEmailAlerts(): Flow<ResponseWrapper<TargetAlerts>> {
        return userRepository.updateEmailAlerts(null)
    }

    fun deleteNumber() = userRepository.updateNumberAlerts(null)

    fun savePushNotificationSetting(isActive: Boolean) { viewModelScope.launch {
        settingsPreferencesRepository.savePushNotificationSetting(isActive)
    }}

    fun getPhoneNumberSetting() = settingsPreferencesRepository.getPhoneNumberSetting()
    fun getPushNotificationSetting() = settingsPreferencesRepository.getPushNotificationSetting()
    fun getEmailNotificationSetting() = settingsPreferencesRepository.getEmailNotificationSetting()
}
