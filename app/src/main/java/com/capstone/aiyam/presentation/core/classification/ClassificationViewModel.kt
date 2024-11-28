package com.capstone.aiyam.presentation.core.classification

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClassificationViewModel @Inject constructor(
    private val chickenRepository: ChickenRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val user = userRepository.getFirebaseUser()

    fun getToken(): Flow<TokenResponse> {
        return when (user) {
            is AuthorizationResponse.Success -> {
                userRepository.getFirebaseToken(user.user)
            }

            is AuthorizationResponse.Error -> {
                throw Exception(user.message)
            }
        }
    }

    fun classify(token: String, file: File, mediaType: String) = chickenRepository.classifyChicken(token, file, mediaType)
}
