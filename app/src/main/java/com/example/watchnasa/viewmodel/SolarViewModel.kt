package com.example.watchnasa.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.repository.RetrofitImpl
import com.example.watchnasa.repository.dto.MarsResponseData
import com.example.watchnasa.repository.dto.SolarFlareResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SolarViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<SolarDataSate> = MutableLiveData()
): ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getLiveData() = liveData

    fun getSolarFlareDataFromServer(startDate: Date, endDate: Date) {
        val startString = dateFormatter.format(startDate)
        val endString = dateFormatter.format(endDate)
        liveData.value = SolarDataSate.Loading(0)
        retrofitImpl.getRetrofitImpl().getSolarFlareData(startString, endString).enqueue(callback)
    }

    private val callback = object: Callback<List<SolarFlareResponseData>>{
        override fun onResponse(
            call: Call<List<SolarFlareResponseData>>,
            response: Response<List<SolarFlareResponseData>>
        ) {
            if (response.isSuccessful && response.body() != null) {
                liveData.value = SolarDataSate.Success(response.body()!!)
            } else {
                liveData.value = SolarDataSate.Error(Throwable("Unsuccessful or empty response!"))
            }
        }
        override fun onFailure(call: Call<List<SolarFlareResponseData>>, t: Throwable) {
            liveData.value = SolarDataSate.Error(t)
        }
    }
}