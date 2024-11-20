package com.capstone.aiyam.presentation.auth.signup

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    fun signUpEmail(fullName: String, email: String, password: String) = authenticationRepository.createAccountWithEmail(fullName, email, password)
}
