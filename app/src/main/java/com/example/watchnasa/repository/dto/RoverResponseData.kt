package com.example.watchnasa.repository.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoverResponseData(
    val id: Long,
    val sol: Int,
    val camera: CameraResponseData,
    @SerializedName("img_src")
    val imgSrc: String,
    @SerializedName("earth_date")
    val earthDate: String
): Parcelable
