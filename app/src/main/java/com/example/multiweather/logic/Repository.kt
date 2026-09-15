package com.example.multiweather.logic

import androidx.lifecycle.liveData
import com.example.multiweather.logic.model.DailyResponse
import com.example.multiweather.logic.model.Weather
import com.example.multiweather.logic.network.MultiWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.async

object Repository {
//    fun <T> liveData(
//    context: CoroutineContext = EmptyCoroutineContext,   // ← 你传的 Dispatchers.IO
//    timeoutInMs: Long = DEFAULT_TIMEOUT,
//    block: suspend LiveDataScope<T>.() -> Unit           // ← 注意：suspend + LiveDataScope 接收者
//): LiveData<T>
//    花括号里可以直接调用任何 suspend 函数，不需要自己 launch / runBlocking / withContext 包一层：
//    你 Repository 里能裸调 MultiWeatherNetwork.searchPlaces(query)（内部是 suspendCoroutine）的原因——挂起能力是构建器送的。
//    块内可以直接 emit(value)，它是 LiveDataScope 上的 suspend 函数，把值发射给 LiveData 的观察者：并且 emit 会自动切回主线程通知观察者（LiveData 的固有语义），你只管在 IO 线程发。
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = MultiWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            Result.success(placeResponse.places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                MultiWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                MultiWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtime = deferredRealtime.await()
            val daily = deferredDaily.await()
            if(realtime.status == "ok" && daily.status == "ok"){
                val weather = Weather(realtime.result.realtime, daily.result.daily)
                Result.success(weather)
            }else{
                Result.failure(RuntimeException("response status is ${realtime.status} or ${daily.status}"))
            }
        }
    }



    private fun <T> fire(context: CoroutineContext , block: suspend () -> Result<T>) = liveData(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }


}