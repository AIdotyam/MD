package com.capstone.aiyam.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classification(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("dead_chicken")
    val deadChicken: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("is_alert")
    val isAlert: Boolean?
): Parcelable
