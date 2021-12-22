package com.example.watchnasa.viewmodel

import com.example.watchnasa.repository.dto.EpicResponseData

sealed class EpicState {
    data class Loading(val process: Int):EpicState()
    data class Success(val epicData: EpicResponseData): EpicState()
    data class Error(val error: Throwable): EpicState()
}