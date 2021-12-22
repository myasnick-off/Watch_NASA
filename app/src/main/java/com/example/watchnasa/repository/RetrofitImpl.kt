package com.example.watchnasa.repository

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitImpl {

    private val baseUrl = "https://api.nasa.gov/"

    private val adapter: NasaAPI by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaAPI::class.java)
    }

    fun getRetrofitImpl() : NasaAPI {
        return adapter
    }
}