package com.capstone.aiyam.presentation.core.onboarding

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.SettingsPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsPreferencesRepository: SettingsPreferencesRepository
): ViewModel() {
    fun saveOnboarding() { CoroutineScope(Dispatchers.IO).launch {
        settingsPreferencesRepository.saveOnBoarding(true)
    }}
}
