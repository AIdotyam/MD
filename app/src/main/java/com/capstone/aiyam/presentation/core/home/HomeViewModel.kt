package com.capstone.aiyam.presentation.core.home

import androidx.lifecycle.ViewModel
import com.capstone.aiyam.domain.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

}
