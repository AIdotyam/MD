package com.capstone.aiyam.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alerts(
    @SerializedName("id")
    val id: Int,
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("is_read")
    val isRead: Boolean
): Parcelable
