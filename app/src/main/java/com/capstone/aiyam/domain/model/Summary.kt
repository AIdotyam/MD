package com.capstone.aiyam.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Summary(
    @SerializedName("id")
    val id: Int,
    @SerializedName("month")
    val month: String,
    @SerializedName("date")
    val date: Int,
    @SerializedName("year")
    val year: Int,
    @SerializedName("media_url")
    val mediaUrl: String
) : Parcelable
