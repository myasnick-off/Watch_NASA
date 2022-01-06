package com.example.watchnasa.repository.dto

data class SolarFlareResponseData(
    val flrID: String = "time_title",
    val beginTime: String,
    val peakTime: String = "",
    val endTime: String = "",
    val classType: String = "",
    val sourceLocation: String = "",
    val link: String = ""
)