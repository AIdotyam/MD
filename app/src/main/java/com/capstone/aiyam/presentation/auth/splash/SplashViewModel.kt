package com.capstone.aiyam.presentation.auth.splash

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    fun getUser() = userRepository.getFirebaseUser()
}
