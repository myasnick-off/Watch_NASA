package com.example.watchnasa.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.repository.RetrofitImpl
import com.example.watchnasa.repository.dto.EpicResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpicViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<EpicState> = MutableLiveData()
): ViewModel() {

    fun getLiveData() = liveData

    fun getEpicImageFromServer() {
        liveData.value = EpicState.Loading(0)
        retrofitImpl.getRetrofitImpl().getEpicImagery().enqueue(callback)
    }

    private val callback = object: Callback<List<EpicResponseData>> {
        override fun onResponse(
            call: Call<List<EpicResponseData>>,
            response: Response<List<EpicResponseData>>
        ) {
            if (response.isSuccessful && response.body() != null) {
                liveData.value = EpicState.Success(response.body()!![0])
            } else {
                liveData.value = EpicState.Error(Throwable("Unsuccessful or empty response!"))
            }
        }
        override fun onFailure(call: Call<List<EpicResponseData>>, t: Throwable) {
            liveData.value = EpicState.Error(t)
        }

    }
}