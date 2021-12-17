package com.example.watchnasa.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApodRetrofitImpl {

    private val baseUrl = "https://api.nasa.gov/"

    fun getRetrofitImpl() : ApodAPI {
        val apodRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return apodRetrofit.create(ApodAPI::class.java)
    }
}