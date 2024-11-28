package com.capstone.aiyam.presentation.core.classificationhistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.model.AuthorizationResponse
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.domain.model.TokenResponse
import com.capstone.aiyam.domain.repository.ChickenRepository
import com.capstone.aiyam.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassificationHistoryViewModel @Inject constructor(
    private val chickenRepository: ChickenRepository
) : ViewModel() {
    private val _histories = MutableStateFlow<ResponseWrapper<List<Classification>>>(ResponseWrapper.Loading)
    val histories: StateFlow<ResponseWrapper<List<Classification>>> = _histories.asStateFlow()

    fun fetchHistories() { viewModelScope.launch {
        chickenRepository.getHistories().collect { _histories.value = it }
    }}
}