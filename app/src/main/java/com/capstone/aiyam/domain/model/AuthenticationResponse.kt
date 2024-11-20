package com.capstone.aiyam.domain.model

sealed interface AuthenticationResponse {
    data object Loading : AuthenticationResponse
    data object Success : AuthenticationResponse
    data class Error(val message: String) : AuthenticationResponse
}
