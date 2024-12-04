package com.capstone.aiyam.presentation.auth.signin

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.security.PrivateKey
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    fun signInEmail(email: String, password: String) = authenticationRepository.loginWithEmail(email, password)
    fun signInGoogle() = authenticationRepository.signInWithGoogle()
}
