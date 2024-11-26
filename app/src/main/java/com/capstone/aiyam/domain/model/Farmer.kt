package com.capstone.aiyam.domain.model

import com.google.gson.annotations.SerializedName

data class Farmer(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)
