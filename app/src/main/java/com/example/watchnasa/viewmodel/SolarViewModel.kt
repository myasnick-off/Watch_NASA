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

// ключи для определения действий
const val SET_KEY = 0       // ключ для получения новых данных взамен старых
const val ADD_KEY = 1       // ключ для добавления новых данных к старым

class SolarViewModel(
    private val retrofitImpl: RetrofitImpl = RetrofitImpl(),
    private val liveData: MutableLiveData<SolarDataSate> = MutableLiveData()
) : ViewModel() {

    private var actionKey: Int = SET_KEY   // переменная для хранения текущего ключа определения действий
    private var data: MutableList<SolarFlareResponseData> = mutableListOf()
    @SuppressLint("SimpleDateFormat")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    fun getLiveData() = liveData

    fun getSolarFlareDataFromServer(startDate: Date, endDate: Date, key: Int) {
        actionKey = key
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
                if (actionKey == ADD_KEY) {
                    // добавляем новые уникальные данные к старым
                    addUniqueData(response.body()!!)
                } else {
                    // помещаем новые данные взамен старых
                    data = (response.body() as MutableList<SolarFlareResponseData>?)!!
                }
                liveData.value = SolarDataSate.Success(addTimeTitlesToList(data))
            } else {
                liveData.value = SolarDataSate.Error(Throwable("Unsuccessful or empty response!"))
            }
        }
        override fun onFailure(call: Call<List<SolarFlareResponseData>>, t: Throwable) {
            liveData.value = SolarDataSate.Error(t)
        }
    }

    // метод добавления в список новых уникальных данных о солнечных вспышках
    private fun addUniqueData(newData: List<SolarFlareResponseData>) {
        for(item in newData) {
            if (!data.contains(item)) {
                data.add(item)
            }
        }
        // сортируем данные в списке по их id
        data = data.sortedBy { it.flrID } as MutableList
    }

    // метод сортировки по интенсивности вспышки по возрастанию
    fun sortByIntensityAsc() {
        val sortedData = data.sortedBy { it.classType }
        liveData.value = SolarDataSate.Success(addClassTitlesToList(sortedData))
    }

    // метод сортировки по интенсивности вспышки по убыванию
    fun sortByIntensityDsc() {
        val sortedData = data.sortedByDescending { it.classType }
        liveData.value = SolarDataSate.Success(addClassTitlesToList(sortedData))
    }

    // мтеод сортировки по дате
    fun sortByDate() {
        val sortedData = data.sortedBy { it.flrID }
        liveData.value = SolarDataSate.Success(addTimeTitlesToList(sortedData))
    }

    // метод добавления в список с данными о солнечных вспышках элементов-заголовков с датами начала вспышек
    private fun addTimeTitlesToList(list: List<SolarFlareResponseData>): MutableList<SolarFlareResponseData> {
        val result: MutableList<SolarFlareResponseData> = arrayListOf()
        var timeTitle = list[0].beginTime.substringBefore('T')
        result.add(SolarFlareResponseData(flrID = "time_title", beginTime = timeTitle))

        for (i in list.indices) {
            if (list[i].beginTime.substringBefore('T') != timeTitle) {
                timeTitle = list[i].beginTime.substringBefore('T')
                result.add(SolarFlareResponseData(flrID = "time_title", beginTime = timeTitle))
            }
            result.add(list[i])
        }
        return result
    }

    // метод добавления в список с данными о солнечных вспышках элементов-заголовков с классом интенсивности
    private fun addClassTitlesToList(list: List<SolarFlareResponseData>): MutableList<SolarFlareResponseData> {
        val result: MutableList<SolarFlareResponseData> = arrayListOf()
        var classTitle = list[0].classType.first()
        result.add(SolarFlareResponseData(flrID = "class_title", classType = "$classTitle class"))

        for (i in list.indices) {
            if (list[i].classType.first() != classTitle) {
                classTitle = list[i].classType.first()
                result.add(SolarFlareResponseData(flrID = "class_title", classType = "$classTitle class"))
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