package com.capstone.aiyam.data.dto

import com.capstone.aiyam.domain.model.Summary
import com.google.gson.annotations.SerializedName

data class SummaryRequest(
    @SerializedName("alerts_summary")
    val alertsSummary: List<Summary>
)
