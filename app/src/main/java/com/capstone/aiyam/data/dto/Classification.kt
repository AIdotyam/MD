package com.capstone.aiyam.data.dto

import com.google.gson.annotations.SerializedName

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
)
