package com.capstone.aiyam.presentation.core.alertdetail

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {
    fun getAlertById(id: Int) = alertRepository.getAlertById(id)
}
