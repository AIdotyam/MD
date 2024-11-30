package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

data class GoogleRequest(
    @SerializedName("token")
    val token: String
)
