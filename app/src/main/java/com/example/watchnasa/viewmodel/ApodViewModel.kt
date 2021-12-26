package com.example.watchnasa.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.repository.dto.ApodResponseData
import com.example.watchnasa.repository.RetrofitImpl
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ApodViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<ApodState> = MutableLiveData()
) : ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getLiveData() = liveData

    fun getAPODFromServer(daysBefore: Int) {
        val dateString = getDateString(daysBefore)
        liveData.value = ApodState.Loading(0)
        retrofitImpl.getRetrofitImpl().getAstronomyPictureOfTheDay(dateString).enqueue(callback)
    }

    fun getAPODByDateFromServer(date: Date) {
        val dateString = dateFormatter.format(date)
        liveData.value = ApodState.Loading(0)
        retrofitImpl.getRetrofitImpl().getAstronomyPictureOfTheDay(dateString).enqueue(callback)
    }

    private val callback = object: Callback<ApodResponseData> {
        override fun onResponse(
            call: Call<ApodResponseData>,
            response: Response<ApodResponseData>
        ) {
            if (response.isSuccessful && response.body() != null) {
                liveData.value = ApodState.Success(response.body()!!)
            } else {
                liveData.value = ApodState.Error(Throwable("Unsuccessful or empty response!"))
            }
        }

        override fun onFailure(call: Call<ApodResponseData>, t: Throwable) {
            liveData.value = ApodState.Error(t)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateString(daysBefore: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, daysBefore * (-1))
        return dateFormatter.format(calendar.time)
    }
}