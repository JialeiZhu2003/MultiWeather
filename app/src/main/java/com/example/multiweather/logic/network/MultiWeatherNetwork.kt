package com.example.multiweather.logic.network

import retrofit2.Call
import retrofit2.await
import kotlin.coroutines.suspendCoroutine

object MultiWeatherNetwork {
    private val placeService = ServiceCreateor.create<PlaceService>()
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : retrofit2.Callback<T> {
                override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resumeWith(Result.success(body))
                    else continuation.resumeWith(Result.failure(RuntimeException("response body is null")))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWith(Result.failure(t))
                }
            })
        }
    }
}