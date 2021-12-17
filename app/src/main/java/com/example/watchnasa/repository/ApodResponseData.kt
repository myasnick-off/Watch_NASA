package com.example.watchnasa.repository

import com.google.gson.annotations.SerializedName

data class ApodResponseData(
    val copyright: String?,
    val date: String?,
    val explanation: String?,
    val hdurl: String?,
    @SerializedName("media_type")
    val mediaType: String?,
    val title: String?,
    val url: String?
)
