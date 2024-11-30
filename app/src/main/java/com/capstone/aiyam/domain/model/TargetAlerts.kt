package com.capstone.aiyam.domain.model

import com.google.gson.annotations.SerializedName

data class TargetAlerts(
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String?,
)
