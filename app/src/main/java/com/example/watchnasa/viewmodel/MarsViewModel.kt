package com.example.watchnasa.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.repository.RetrofitImpl
import com.example.watchnasa.repository.dto.MarsResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MarsViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<MarsDataState> = MutableLiveData()
): ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getLiveData() = liveData

    fun getMarsPhotoFromServer(date: Date) {
        val dateString = dateFormatter.format(date)
        liveData.value = MarsDataState.Loading(0)
        retrofitImpl.getRetrofitImpl().getMarsRoverPhotos("2021-12-20", null).enqueue(callback)
    }

    private val callback = object: Callback<MarsResponseData> {
        override fun onResponse(
            call: Call<MarsResponseData>,
            response: Response<MarsResponseData>
        ) {
            if (response.isSuccessful && response.body() != null) {
                liveData.value = MarsDataState.Success(response.body()!!)
            } else {
                liveData.value = MarsDataState.Error(Throwable("Unsuccessful or empty response!"))
            }
        }
        override fun onFailure(call: Call<MarsResponseData>, t: Throwable) {
            liveData.value = MarsDataState.Error(t)
        }

    }
}