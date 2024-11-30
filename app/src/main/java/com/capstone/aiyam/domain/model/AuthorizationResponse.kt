package com.capstone.aiyam.domain.model

import com.google.firebase.auth.FirebaseUser

sealed interface AuthorizationResponse {
    data class Success(val user: FirebaseUser) : AuthorizationResponse
    data class Error(val message: String) : AuthorizationResponse
}
