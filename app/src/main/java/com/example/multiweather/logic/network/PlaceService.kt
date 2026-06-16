package com.example.multiweather.logic.network

import com.example.multiweather.MultiWeatherApplication
import com.example.multiweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${MultiWeatherApplication.TOKEN}&lang=zh_CN")
    suspend fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}