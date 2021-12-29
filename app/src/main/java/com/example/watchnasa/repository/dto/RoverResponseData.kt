package com.example.watchnasa.repository.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoverResponseData(
    val id: Int,
    val name: String,
    @SerializedName("landing_date")
    val landingDate: String,
    val status: String
): Parcelable
