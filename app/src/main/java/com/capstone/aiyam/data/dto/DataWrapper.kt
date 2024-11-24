package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

class DataWrapper<T> (
    @SerializedName("data")
    val data: T,
)
