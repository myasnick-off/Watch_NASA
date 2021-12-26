package com.example.watchnasa.viewmodel

import com.example.watchnasa.repository.dto.ApodResponseData

sealed class ApodState {
    data class Loading(val progress: Int) : ApodState()
    data class Success(val apodData: ApodResponseData) : ApodState()
    data class Error(val error: Throwable) : ApodState()
}
