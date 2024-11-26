package com.capstone.aiyam.presentation.core.alerts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {
    private val _alerts = MutableStateFlow<ResponseWrapper<List<Alerts>>>(ResponseWrapper.Loading)
    val alerts: StateFlow<ResponseWrapper<List<Alerts>>> = _alerts.asStateFlow()

    private val _filterCriteria = MutableStateFlow<String?>(null)
    val filteredAlerts = combine(_alerts, _filterCriteria) { response, filter ->
        if (response is ResponseWrapper.Success && !filter.isNullOrEmpty()) {
            val filteredData = response.data.filter {
                when (filter) {
                    "read" -> it.isRead
                    "unread" -> !it.isRead
                    else -> true
                }
            }
            Log.d("Filtered Alerts", filteredData.toString())
            ResponseWrapper.Success(filteredData)
        } else {
            response
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, ResponseWrapper.Loading)

    init {
        fetchAlerts()
    }

    private fun fetchAlerts() { viewModelScope.launch {
        alertRepository.getAlerts().collect { _alerts.value = it }
    }}

    fun refreshAlerts() {
        fetchAlerts()
    }

    fun setFilterCriteria(criteria: String?) {
        _filterCriteria.value = criteria
    }
}
