package com.capstone.aiyam.presentation.auth.profile

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthorizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authorizationRepository: AuthorizationRepository
) : ViewModel() {
    fun getUser() = authorizationRepository.getUser()
    fun signOut() = authorizationRepository.signOut()
}
