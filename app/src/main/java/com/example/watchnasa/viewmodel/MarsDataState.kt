package com.example.watchnasa.viewmodel

import com.example.watchnasa.repository.dto.MarsResponseData

sealed class MarsDataState {
    data class Loading(val progress: Int) : MarsDataState()
    data class Success(val marsData: MarsResponseData) : MarsDataState()
    data class Error(val error: Throwable) : MarsDataState()
}
