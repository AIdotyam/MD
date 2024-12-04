package com.capstone.aiyam.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class WeeklySummary(
    val date: LocalDate,
    val alertCount: Int
) {
    val formattedDate: String
        get() = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
