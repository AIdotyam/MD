package com.capstone.aiyam.utils

import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private suspend fun getToken(
    user: AuthorizationResponse,
    tokenHandler: (FirebaseUser) -> Flow<TokenResponse>
): TokenResponse {
    return when (user) {
        is AuthorizationResponse.Success -> {
            tokenHandler(user.user).first()
        }
        is AuthorizationResponse.Error -> {
            throw IllegalAccessException(user.message)
        }
    }
}

suspend fun <T> withToken(
    user: AuthorizationResponse,
    tokenHandler: (FirebaseUser) -> Flow<TokenResponse>,
    block: suspend (String) -> T
): T {
    return when (val tokenResponse = getToken(user, tokenHandler)) {
        is TokenResponse.Success -> {
            val authHeader = "Bearer ${tokenResponse.token}"
            block(authHeader)
        }
        is TokenResponse.Failed -> throw IllegalAccessException("Failed to obtain token")
    }
}