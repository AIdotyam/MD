package com.capstone.aiyam.presentation.core.alerts

import com.capstone.aiyam.domain.model.Alerts

sealed class AlertsDisplayItem {
    data class Header(val date: String) : AlertsDisplayItem()
    data class Item(val alerts: Alerts) : AlertsDisplayItem()
}
