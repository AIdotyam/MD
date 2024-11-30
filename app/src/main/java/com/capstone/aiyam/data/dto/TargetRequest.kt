package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

data class TargetRequest (
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String?,
)
