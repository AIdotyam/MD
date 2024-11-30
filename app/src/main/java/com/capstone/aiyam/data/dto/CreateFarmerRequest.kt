package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

data class CreateFarmerRequest(
    @SerializedName("uid")
    val uid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)
