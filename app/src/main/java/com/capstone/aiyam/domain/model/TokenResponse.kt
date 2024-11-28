package com.capstone.aiyam.domain.model

sealed interface TokenResponse {
    data class Success(val token: String) : TokenResponse
    data object Failed : TokenResponse
}
