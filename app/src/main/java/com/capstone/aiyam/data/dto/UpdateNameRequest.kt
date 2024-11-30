package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateNameRequest(
    @SerializedName("name")
    val name: String
)
