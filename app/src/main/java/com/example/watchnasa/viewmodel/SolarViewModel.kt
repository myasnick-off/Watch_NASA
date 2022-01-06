package com.example.watchnasa.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.watchnasa.repository.RetrofitImpl
import com.example.watchnasa.repository.dto.SolarFlareResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SolarViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<SolarDataSate> = MutableLiveData()
) : ViewModel() {

    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getLiveData() = liveData

    fun getSolarFlareDataFromServer(startDate: Date, endDate: Date) {
        val startString = dateFormatter.format(startDate)
        val endString = dateFormatter.format(endDate)
        liveData.value = SolarDataSate.Loading(0)
        retrofitImpl.getRetrofitImpl().getSolarFlareData(startString, endString).enqueue(callback)
    }

    private val callback = object : Callback<List<SolarFlareResponseData>> {
        override fun onResponse(
            call: Call<List<SolarFlareResponseData>>,
            response: Response<List<SolarFlareResponseData>>
        ) {
            if (response.isSuccessful && response.body() != null) {
                val data = addTimeTitlesToList(response.body()!!)
                liveData.value = SolarDataSate.Success(data)
            } else {
                liveData.value = SolarDataSate.Error(Throwable("Unsuccessful or empty response!"))
            }
        }
        override fun onFailure(call: Call<List<SolarFlareResponseData>>, t: Throwable) {
            liveData.value = SolarDataSate.Error(t)
        }
    }

    // метод добавления в список с данными о солнечных вспышках элементов-заголовков с датами начала вспышек
    private fun addTimeTitlesToList(list: List<SolarFlareResponseData>): List<SolarFlareResponseData> {
        val result: ArrayList<SolarFlareResponseData> = arrayListOf()
        var timeTitle = list[0].beginTime.substringBefore('T')
        result.add(SolarFlareResponseData(beginTime = timeTitle))

        for (i in list.indices) {
            if (list[i].beginTime.substringBefore('T') != timeTitle) {
                timeTitle = list[i].beginTime.substringBefore('T')
                result.add(SolarFlareResponseData(beginTime = timeTitle))
            }
            result.add(list[i])
        }
        return result
    }

    // метод создания списка-заглушки с данными на случай неполадок на сервере
    private fun getMocData(): List<SolarFlareResponseData> {
        val mocData: ArrayList<SolarFlareResponseData> = arrayListOf()
        for (i in 0..9) {
            mocData.add(SolarFlareResponseData("$i", "", "", "", "", "", ""))
        }
        return mocData
    }
}