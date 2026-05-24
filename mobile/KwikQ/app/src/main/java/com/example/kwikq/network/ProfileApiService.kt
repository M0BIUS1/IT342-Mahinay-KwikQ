package com.example.kwikq.network

import com.example.kwikq.network.models.UserProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApiService {
    @GET("api/profile")
    fun getProfile(): Call<UserProfile>

    @PUT("api/profile")
    fun updateProfile(@Body profile: UserProfile): Call<UserProfile>
}
