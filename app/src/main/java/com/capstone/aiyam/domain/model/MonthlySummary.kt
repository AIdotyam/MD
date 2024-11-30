package com.capstone.aiyam.domain.model

import com.google.gson.annotations.SerializedName

data class MonthlySummary(
    @SerializedName("month")
    val month: String,
    @SerializedName("dead_count")
    val deadCount: Int
)
