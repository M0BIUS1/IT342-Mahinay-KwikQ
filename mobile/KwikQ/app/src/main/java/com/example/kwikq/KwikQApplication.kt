package com.example.kwikq

import android.app.Application
import com.example.kwikq.network.RetrofitClient

class KwikQApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Retrofit client with JWT interceptor
        RetrofitClient.initialize(this)
    }
}
