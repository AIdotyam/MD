package com.capstone.aiyam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsPreferencesRepository: SettingsPreferencesRepository
) : ViewModel() {
    var splashCondition = MutableStateFlow(true)
        private set

    private val hasOnboarding = MutableStateFlow(false)
    private val isAuthenticated = MutableStateFlow(false)

    val startDestination = combine(hasOnboarding, isAuthenticated) { hasOnboarding, isAuthenticated ->
        when {
            !hasOnboarding -> R.id.onboardingFragment
            isAuthenticated -> R.id.homeFragment
            else -> R.id.signinFragment
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, R.id.signinFragment)

    init {
        viewModelScope.launch {
            hasOnboarding.value = settingsPreferencesRepository.getOnBoarding().first()
            isAuthenticated.value = when (getUser()) {
                is AuthorizationResponse.Success -> true
                is AuthorizationResponse.Error -> false
            }

            delay(1000)

            splashCondition.value = false
        }
    }

    private fun getUser() = userRepository.getFirebaseUser()
}
