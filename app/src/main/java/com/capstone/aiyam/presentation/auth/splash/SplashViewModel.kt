package com.capstone.aiyam.presentation.auth.splash

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {
    fun getUser() = authorizationRepository.getUser()
}
