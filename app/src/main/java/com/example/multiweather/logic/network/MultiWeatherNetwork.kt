package com.example.multiweather.logic.network

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Call
import retrofit2.await
import kotlin.coroutines.suspendCoroutine

object MultiWeatherNetwork {
    private val placeService = ServiceCreateor.create<PlaceService>()
    private val weatherService = ServiceCreateor.create<WeatherService>()
    //彩云免费版 QPS = 1，所有请求必须串行且间隔 ≥1 秒，否则返回 429
    private const val MIN_INTERVAL_MS = 1100L
    private val rateLimitMutex = Mutex()
    private var lastRequestAt = 0L
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()
    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()
    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    private suspend fun <T> Call<T>.await(): T = rateLimitMutex.withLock {
        //拿不到锁就挂起；与上一次请求间隔不足 1.1 秒就补齐差额
        val wait = MIN_INTERVAL_MS - (System.currentTimeMillis() - lastRequestAt)
        if (wait > 0) delay(wait)
        lastRequestAt = System.currentTimeMillis()
        suspendCoroutine { continuation ->
            enqueue(object : retrofit2.Callback<T> {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resumeWith(Result.success(body))
                    } else {
                        //失败时把状态码、完整 URL、服务器返回的错误原文都打出来，便于定位
                        val errorBody = try {
                            response.errorBody()?.string()
                        } catch (e: Exception) {
                            null
                        }
                        Log.e("MultiWeatherNetwork", "HTTP ${response.code()} url=${call.request().url()} error=$errorBody")
                        continuation.resumeWith(Result.failure(RuntimeException("code=${response.code()} url=${call.request().url()} error=$errorBody")))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWith(Result.failure(t))
                }
            }) // object 它等价于 Java 的匿名内部类
//            流程：suspendCoroutine 让协程暂停 → enqueue 把请求丢到后台线程 → 结果回来（主线程回调）→ continuation.resumeWith(...) 把协程唤醒，await() 就此返回或抛出。
//            这个匿名对象就是一个"转接头"：把 Retrofit 的"两方法回调"接到 Kotlin 协程的"续体（continuation）"上。没有它，回调的结果没法交还给协程。
        }
    }
}