package com.example.kwikq.network

import android.content.Context
import com.example.kwikq.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var context: Context? = null
    private var retrofit: Retrofit? = null

    fun initialize(appContext: Context) {
        context = appContext
    }

    val authApiService: AuthApiService
        get() = getRetrofitInstance().create(AuthApiService::class.java)

    private fun getRetrofitInstance(): Retrofit {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
            
            // Add JWT interceptor if context is available
            if (context != null) {
                okHttpClient.addInterceptor(JwtInterceptor(context!!))
            }
            
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
