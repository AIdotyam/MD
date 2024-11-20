package com.capstone.aiyam.domain.repository

import com.capstone.aiyam.domain.model.AuthorizationResponse
import kotlinx.coroutines.flow.Flow

interface AuthorizationRepository {
    fun getUser(): Flow<AuthorizationResponse>
}
