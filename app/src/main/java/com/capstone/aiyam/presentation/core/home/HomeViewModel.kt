package com.capstone.aiyam.presentation.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.capstone.aiyam.domain.repository.AlertRepository
import com.capstone.aiyam.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {
    private val currentDate = LocalDate.now()
    private val month = currentDate.monthValue
    private val year = currentDate.year

    private val _daily = MutableStateFlow<ResponseWrapper<List<DailySummary>>>(ResponseWrapper.Loading)
    val daily: StateFlow<ResponseWrapper<List<DailySummary>>> = _daily.asStateFlow()

    private val _monthly = MutableStateFlow<ResponseWrapper<List<MonthlySummary>>>(ResponseWrapper.Loading)
    val monthly: StateFlow<ResponseWrapper<List<MonthlySummary>>> = _monthly.asStateFlow()

    private val _weeklyAlerts = MutableStateFlow<ResponseWrapper<List<Alerts>>>(ResponseWrapper.Loading)
    val weeklyAlerts: StateFlow<ResponseWrapper<List<Alerts>>> = _weeklyAlerts.asStateFlow()

    init {
        init()
    }

    fun init() {
        val month = currentDate.monthValue
        val year = currentDate.year

        getDailySummaries(year, month)
        getMonthlySummaries(year)
    }

    fun getWeeklySummaries(year: Int, month: Int) { viewModelScope.launch {
        alertRepository.getAlerts().collect {
            _weeklyAlerts.value = it
        }
    }}

    fun paginateDataPerWeek(data: List<Alerts>): Map<String, Int> {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val weekFields = WeekFields.of(Locale.getDefault())

        return data
            .map { item ->
                val dateTime = LocalDateTime.parse(item.createdAt, formatter)
                val weekNumber = dateTime.get(weekFields.weekOfWeekBasedYear())
                val year = dateTime.year

                "$year-W$weekNumber" to item
            }
            .groupingBy { it.first }
            .eachCount()
    }

    fun getDailySummaries(year: Int, month: Int) { viewModelScope.launch {

    }}

    fun getMonthlySummaries(year: Int) { viewModelScope.launch {

    }}

    fun getPushToken() { viewModelScope.launch {

    }}
}
