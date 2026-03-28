package com.example.kwikq.network

import android.content.Context
import com.example.kwikq.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get the JWT token from session
        val token = sessionManager.getToken()
        
        // If token exists, add it to the Authorization header
        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(request)
    }
}
