package com.capstone.aiyam.presentation.auth.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TargetAlerts
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsPreferencesRepository: SettingsPreferencesRepository
) : ViewModel() {
    fun getUser() = userRepository.getFirebaseUser()
    private fun getFcmToken() = userRepository.getPushToken()
    suspend fun signOut() { withContext(Dispatchers.IO) {
        userRepository.firebaseSignOut()
        settingsPreferencesRepository.savePhoneNumberSetting("")
        settingsPreferencesRepository.savePushNotificationSetting(false)
        settingsPreferencesRepository.saveEmailNotificationSetting(false)
        settingsPreferencesRepository.saveTelegramNotificationSetting(false)
    }}

    init {
        initializeTarget().launchIn(viewModelScope)
    }

    private fun initializeTarget() = userRepository.getTargetAlerts()

    fun enablePushAlerts(): Flow<ResponseWrapper<TargetAlerts>> = flow {
        when (val token = getFcmToken().first()) {
            is TokenResponse.Success -> {
                userRepository.updatePushAlerts(token.token).collect { emit(it) }
            }

            is TokenResponse.Failed -> {
                emit(ResponseWrapper.Error("Failed to get FCM token"))
            }
        }
    }

    fun disablePushAlerts(): Flow<ResponseWrapper<TargetAlerts>> {
        return userRepository.updatePushAlerts(null)
    }

    fun deleteNumber() = userRepository.updateNumberAlerts(null)

    fun getPhoneNumberSetting() = settingsPreferencesRepository.getPhoneNumberSetting()
    fun getPushNotificationSetting() = settingsPreferencesRepository.getPushNotificationSetting()
}
