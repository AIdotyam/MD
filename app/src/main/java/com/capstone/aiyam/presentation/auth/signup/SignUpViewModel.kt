package com.capstone.aiyam.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.domain.model.AuthenticationResponse
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO)
    fun createTargetAlerts() { scope.launch {
        userRepository.createTargetAlerts().collect{}
    }}

    fun signUpEmail(fullName: String, email: String, password: String): Flow<AuthenticationResponse> {
        return authenticationRepository.createAccountWithEmail(fullName, email, password)
    }
}
