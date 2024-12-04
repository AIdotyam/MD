package com.capstone.aiyam.presentation.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.model.WeeklySummary
import com.capstone.aiyam.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val pageSize = 7 // Days per page

    // StateFlows to manage UI states
    private val _weeklySummaries = MutableStateFlow<List<WeeklySummary>>(emptyList())
    private val _currentPage = MutableStateFlow(0)
    private val _isLoading = MutableStateFlow(false)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Derived state for current page's summaries
    val currentPageSummaries: StateFlow<List<WeeklySummary>> = combine(
        _weeklySummaries,
        _currentPage
    ) { summaries, page ->
        val start = page * pageSize
        val end = minOf(start + pageSize, summaries.size)
        if (start >= summaries.size) {
            emptyList()
        } else {
            summaries.subList(start, end)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Derived states for button enablement
    val canNavigateNext: StateFlow<Boolean> = combine(
        _weeklySummaries,
        _currentPage
    ) { summaries, page ->
        val totalPages = (summaries.size + pageSize - 1) / pageSize
        page < totalPages - 1
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val canNavigatePrevious: StateFlow<Boolean> = _currentPage.map { page ->
        page > 0
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        fetchWeeklySummaries()
    }

    /**
     * Fetches alerts from the repository, aggregates them per week,
     * and updates the summaries.
     */
    private fun fetchWeeklySummaries() {
        viewModelScope.launch {
            alertRepository.getAlerts()
                .onStart {
                    _isLoading.value = true
                    _errorMessage.value = null
                }
                .catch { e ->
                    _isLoading.value = false
                    _errorMessage.value = e.message ?: "An unexpected error occurred."
                }
                .collect { response ->
                    when (response) {
                        is ResponseWrapper.Success -> {
                            _isLoading.value = false
                            val summaries = aggregateAlertsByDay(response.data)
                            _weeklySummaries.value = summaries
                            _currentPage.value = 0
                        }
                        is ResponseWrapper.Error -> {
                            _isLoading.value = false
                            _errorMessage.value = response.error
                        }
                        is ResponseWrapper.Loading -> {
                            _isLoading.value = true
                            _errorMessage.value = null
                        }
                    }
                }
        }
    }

    /**
     * Aggregates the list of alerts into weekly summaries.
     */
    private fun aggregateAlertsByDay(alerts: List<Alerts>): List<WeeklySummary> {
        return alerts.groupBy { alert ->
            val dateTime = LocalDateTime.parse(alert.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            dateTime.toLocalDate() // Extracts the date part
        }.map { (date, alerts) ->
            WeeklySummary(date = date, alertCount = alerts.size)
        }.sortedByDescending { it.date } // Sort days in ascending order
    }

    /**
     * Navigates to the next page if possible.
     */
    fun goToNextPage() {
        val totalPages = (_weeklySummaries.value.size + pageSize - 1) / pageSize
        if (_currentPage.value < totalPages - 1) {
            _currentPage.value += 1
        }
    }

    /**
     * Navigates to the previous page if possible.
     */
    fun goToPreviousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value -= 1
        }
    }

    /**
     * Refreshes the data by re-fetching alerts.
     */
    fun refreshData() {
        fetchWeeklySummaries()
    }
}