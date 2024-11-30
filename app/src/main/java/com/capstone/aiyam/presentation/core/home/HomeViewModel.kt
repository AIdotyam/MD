package com.capstone.aiyam.presentation.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.aiyam.data.dto.ResponseWrapper
import com.capstone.aiyam.domain.model.DailySummary
import com.capstone.aiyam.domain.model.MonthlySummary
import com.capstone.aiyam.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    val currentDate = LocalDate.now()

    private val _daily = MutableStateFlow<ResponseWrapper<List<DailySummary>>>(ResponseWrapper.Loading)
    val daily: StateFlow<ResponseWrapper<List<DailySummary>>> = _daily.asStateFlow()

    private val _monthly = MutableStateFlow<ResponseWrapper<List<MonthlySummary>>>(ResponseWrapper.Loading)
    val monthly: StateFlow<ResponseWrapper<List<MonthlySummary>>> = _monthly.asStateFlow()

    init {
        init()
    }

    fun init() {
        val weekOfMonth = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfMonth())
        val month = currentDate.monthValue
        val year = currentDate.year

        getDailySummaries(year, month, weekOfMonth)
        getMonthlySummaries(year)
    }

    fun getDailySummaries(year: Int, month: Int, week: Int) { viewModelScope.launch {
        dashboardRepository.getDailySummaries(year, month, week).collect { _daily.value = it }
    }}

    fun getMonthlySummaries(year: Int) { viewModelScope.launch {
        dashboardRepository.getMonthlySummaries(year).collect { _monthly.value = it }
    }}
}
