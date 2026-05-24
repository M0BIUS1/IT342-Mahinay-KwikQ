package com.example.kwikq.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AnalyticsApiService {
    @POST("api/analytics/retry")
    fun sendRetry(@Body payload: Map<String, Any>): Call<Void>
}
