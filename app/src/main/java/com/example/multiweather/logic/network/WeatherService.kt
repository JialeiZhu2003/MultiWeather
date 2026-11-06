package com.example.multiweather.logic.network
import com.example.multiweather.MultiWeatherApplication
import com.example.multiweather.logic.model.DailyResponse
import com.example.multiweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface WeatherService {
    // https://api.caiyunapp.com/v2.5/cpfGS3sHvrcgovb4/116.4073963,39.9041999/realtime.json
    @GET("v2.5/${MultiWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>
    @GET("v2.5/${MultiWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}
