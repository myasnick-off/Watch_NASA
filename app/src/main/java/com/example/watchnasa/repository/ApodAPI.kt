package com.example.watchnasa.repository

import com.example.watchnasa.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс API запроса на получение Astronomy Picture of the Day (APOD)
interface ApodAPI {
    @GET("planetary/apod")
    fun getAstronomyPictureOfTheDay(
        @Query("date") date: String?,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): Call<ApodResponseData>
}