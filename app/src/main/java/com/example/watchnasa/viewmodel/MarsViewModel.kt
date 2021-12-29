package com.example.watchnasa.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.R
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

    fun getMarsPhotoFromServer(roverNameId: Int, date: Date) {
        val dateString = dateFormatter.format(date)
        liveData.value = MarsDataState.Loading(0)
        when(roverNameId) {
            R.string.rover_opportunity -> {
                retrofitImpl.getRetrofitImpl().getRoverOpportunityPhotos(dateString, null).enqueue(callback)
            }
            R.string.rover_spirit -> {
                retrofitImpl.getRetrofitImpl().getRoverSpiritPhotos(dateString, null).enqueue(callback)
            }
            else -> {
                retrofitImpl.getRetrofitImpl().getRoverCuriosityPhotos(dateString, null).enqueue(callback)
            }
        }
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