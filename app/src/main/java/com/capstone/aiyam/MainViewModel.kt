package com.capstone.aiyam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var splashCondition = MutableStateFlow(true)
        private set

    var isAuthenticated = MutableStateFlow(false)
        private set

    private fun getUser() = userRepository.getFirebaseUser()

    init { viewModelScope.launch {
        when (getUser()) {
            is AuthorizationResponse.Success -> {
                isAuthenticated.value = true
            }
            is AuthorizationResponse.Error -> {
                isAuthenticated.value = false
            }
        }

        delay(1000)

        splashCondition.value = false
    }}
}
