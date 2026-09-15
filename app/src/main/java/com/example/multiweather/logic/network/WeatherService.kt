package com.example.multiweather.logic.network
import com.example.multiweather.MultiWeatherApplication
import com.example.multiweather.logic.model.DailyResponse
import com.example.multiweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface WeatherService {
    @GET("v2/realtime?token=${MultiWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>
    @GET("v2/daily?token=${MultiWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}