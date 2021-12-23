package com.example.watchnasa.viewmodel

import com.example.watchnasa.repository.dto.SolarFlareResponseData

sealed class SolarDataSate {
    data class Loading(val process: Int):SolarDataSate()
    data class Success(val solarData: List<SolarFlareResponseData>): SolarDataSate()
    data class Error(val error: Throwable): SolarDataSate()
}