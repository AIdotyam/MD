package com.capstone.aiyam.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classification(
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("dead_count")
    val deadCount: Int,
    @SerializedName("chicken_count")
    val chickenCount: Int,
    @SerializedName("crowd_density")
    val crowdDensity: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("is_alert")
    val isAlert: Boolean
): Parcelable
