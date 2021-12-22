package com.example.watchnasa.repository.dto

import com.google.gson.annotations.SerializedName

data class RoverResponseData(
    val id: Long,
    val sol: Int,
    @SerializedName("img_src")
    val imgSrc: String,
    @SerializedName("earth_date")
    val earthDate: String
)
