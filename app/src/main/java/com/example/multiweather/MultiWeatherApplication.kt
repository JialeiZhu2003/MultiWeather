package com.example.multiweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.annotation.UiContext

class MultiWeatherApplication : Application(){
    companion object{
        const val TOKEN = "cpfGS3sHvrcgovb4"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}