package com.example.watchnasa.repository

import com.example.watchnasa.BuildConfig
import com.example.watchnasa.repository.dto.ApodResponseData
import com.example.watchnasa.repository.dto.EpicResponseData
import com.example.watchnasa.repository.dto.MarsResponseData
import com.example.watchnasa.repository.dto.SolarFlareResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Интерфейс API запросов NASA
interface NasaAPI {

    // запрос на получение Astronomy Picture of the Day (APOD)
    @GET("planetary/apod")
    fun getAstronomyPictureOfTheDay(
        @Query("date") date: String?,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): Call<ApodResponseData>

    // запрос на получение ежедневной коллекции снимков от Earth Polychromatic Imaging Camera (EPIC)
    @GET("EPIC/api/natural/images")
    fun getEpicImagery(
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ):Call<List<EpicResponseData>>

    // запрос на получение фото с камер аппарата Curiosity
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    fun getMarsRoverPhotos(
        @Query("earth_date") date: String,
        @Query("camera") camera: String?,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): Call<MarsResponseData>

    // запрос на получение данных о солнечных вспышках за период
    @GET("DONKI/FLR")
    fun getSolarFlareData(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY
    ): Call<List<SolarFlareResponseData>>
}