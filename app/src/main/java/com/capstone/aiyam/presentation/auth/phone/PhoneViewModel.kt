package com.capstone.aiyam.presentation.auth.phone

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AuthenticationRepository
import com.capstone.aiyam.domain.repository.UserRepository
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhoneViewModel @Inject constructor (
    private val userRepository: UserRepository
) : ViewModel() {
    fun postNumber(phoneNumber: String) = userRepository.updateNumberAlerts(phoneNumber)
}
