package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse

interface AuthorizationRepository {
    fun getUser(): AuthorizationResponse
    fun signOut()
}
