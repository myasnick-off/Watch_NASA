package com.example.watchnasa.repository.dto

import com.google.gson.annotations.SerializedName

data class CameraResponseData(
    val id: Int,
    val name: String,
    @SerializedName("rover_id")
    val roverId: Int,
    @SerializedName("full_name")
    val fullName: String
)