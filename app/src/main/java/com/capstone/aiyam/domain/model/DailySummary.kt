package com.capstone.aiyam.domain.model

import com.google.gson.annotations.SerializedName

data class DailySummary(
    @SerializedName("day")
    val day: String,
    @SerializedName("dead_count")
    val deadCount: Int,
    @SerializedName("crowd_density")
    val crowdDensity: String,
    @SerializedName("chicken_count")
    val chickenCount: Int,
    @SerializedName("is_alert")
    val isAlert: Boolean
)
