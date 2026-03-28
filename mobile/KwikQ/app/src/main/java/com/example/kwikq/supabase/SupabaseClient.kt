package com.example.kwikq.supabase

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object SupabaseClientManager {
    private const val SUPABASE_URL = "https://neosrhcsogeqsgnatvvb.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5lb3NyaGNzb2dlcXNnbmF0dnZiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI4MzgwMjEsImV4cCI6MjA4ODQxNDAyMX0.2FQJgvTGR0L8tRpBSPVJvu5Rq3URzwsyKrMr-wsfuCA"
    private const val TAG = "SupabaseClient"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()

    /**
     * Sign up with email and password
     */
    suspend fun signUp(email: String, password: String): SupabaseAuthResponse? {
        return try {
            val payload = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            
            val jsonData = gson.toJson(payload)
            Log.d(TAG, "SignUp request: $jsonData")
            
            val requestBody = jsonData.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$SUPABASE_URL/auth/v1/signup")
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            Log.d(TAG, "Making signup request to: $SUPABASE_URL/auth/v1/signup")
            
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            
            Log.d(TAG, "Signup response code: ${response.code}")
            
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return null
                Log.d(TAG, "Signup response: $body")
                gson.fromJson(body, SupabaseAuthResponse::class.java)
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e(TAG, "Signup failed: ${response.code} - $errorBody")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "SignUp IO error: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "SignUp error: ${e.message}", e)
            null
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): SupabaseAuthResponse? {
        return try {
            val payload = JsonObject().apply {
                addProperty("email", email)
                addProperty("password", password)
            }
            
            val jsonData = gson.toJson(payload)
            Log.d(TAG, "SignIn request: $jsonData")
            
            val requestBody = jsonData.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$SUPABASE_URL/auth/v1/token?grant_type=password")
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            Log.d(TAG, "Making signin request to: $SUPABASE_URL/auth/v1/token")
            
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            
            Log.d(TAG, "SignIn response code: ${response.code}")
            
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return null
                Log.d(TAG, "SignIn response: $body")
                val authResponse = gson.fromJson(body, SupabaseAuthResponse::class.java)
                // For token endpoint, the user info is in 'user' field, token in 'access_token'
                if (authResponse.access_token != null && authResponse.user == null) {
                    authResponse.session = SupabaseSession(
                        accessToken = authResponse.access_token,
                        tokenType = authResponse.token_type ?: "bearer"
                    )
                }
                authResponse
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                Log.e(TAG, "SignIn failed: ${response.code} - $errorBody")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "SignIn IO error: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "SignIn error: ${e.message}", e)
            null
        }
    }

    /**
     * Sign out
     */
    suspend fun signOut(token: String): Boolean {
        return try {
            val request = Request.Builder()
                .url("$SUPABASE_URL/auth/v1/logout")
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer $token")
                .post("".toRequestBody("application/json".toMediaType()))
                .build()
            
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            
            Log.d(TAG, "SignOut response code: ${response.code}")
            response.isSuccessful
        } catch (e: IOException) {
            Log.e(TAG, "SignOut IO error: ${e.message}", e)
            false
        }
    }
}

data class SupabaseAuthResponse(
    val access_token: String? = null,
    val token_type: String? = null,
    val expires_in: Int? = null,
    val refresh_token: String? = null,
    val user: SupabaseUser? = null,
    var session: SupabaseSession? = null
)

data class SupabaseSession(
    val accessToken: String,
    val tokenType: String = "bearer"
)

data class SupabaseUser(
    val id: String? = null,
    val email: String? = null,
    val user_metadata: Map<String, Any>? = null
)
