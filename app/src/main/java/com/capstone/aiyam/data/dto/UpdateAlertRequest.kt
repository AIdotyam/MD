package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateAlertRequest(
    @SerializedName("is_read")
    val isRead: Boolean
)
