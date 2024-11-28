package com.capstone.aiyam.presentation.core.classification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClassificationViewModel @Inject constructor(
    private val chickenRepository: ChickenRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val user = userRepository.getFirebaseUser()

    fun getToken(): Flow<TokenResponse> = flow {
        when (user) {
            is AuthorizationResponse.Success -> {
                userRepository.getFirebaseToken(user.user)
            }

            is AuthorizationResponse.Error -> {
                emit(TokenResponse.Failed)
            }
        }
    }

    fun classify(token: String, file: File, mediaType: String) = chickenRepository.classifyChicken(token, file, mediaType)
}
