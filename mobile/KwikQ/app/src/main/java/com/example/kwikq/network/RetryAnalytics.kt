package com.example.kwikq.network

import android.util.Log
import com.example.kwikq.BuildConfig
import java.util.*

object RetryAnalytics {
    private const val TAG = "RetryAnalytics"
    // Test hook: when set, analytics events will be routed to this lambda instead of network
    @JvmStatic
    var analyticsSender: ((Map<String, Any>) -> Unit)? = null

    fun sendEventIfEnabled(payload: Map<String, Any>) {
        // Read prefs via RetrofitClient context if available
        try {
            val enabled = tryGetPrefBoolean("analytics_enabled", false)
            if (!enabled) return
            val sample = tryGetPrefInt("analytics_sample_percent", 10)
            val rand = Random()
            if (sample <= 0) return
            if (rand.nextInt(100) >= sample) {
                // not sampled
                return
            }

            // enrich payload
            val body = HashMap(payload)
            body["app"] = "KwikQ"
            body["env"] = if (BuildConfig.DEBUG) "debug" else "prod"
            body["ts"] = System.currentTimeMillis()

            // If a test hook is provided, use it. Otherwise send async via Retrofit
            analyticsSender?.let {
                try {
                    it(body)
                    Log.d(TAG, "sent retry analytics via test hook")
                } catch (t: Throwable) {
                    Log.w(TAG, "failed sending retry analytics via hook: ${t.localizedMessage}")
                }
            } ?: run {
                val call = RetrofitClient.analyticsApiService.sendRetry(body)
                call.enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        Log.d(TAG, "sent retry analytics: ${response.code()}")
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Log.w(TAG, "failed sending retry analytics: ${t.localizedMessage}")
                    }
                })
            }
        } catch (e: Throwable) {
            // don't let analytics break app
            Log.w(TAG, "analytics error: ${e.localizedMessage}")
        }
    }

    private fun tryGetPrefBoolean(key: String, def: Boolean): Boolean {
        return try {
            RetrofitClient::class.java.getDeclaredField("context").apply { isAccessible = true }
            val ctxField = RetrofitClient::class.java.getDeclaredField("context")
            ctxField.isAccessible = true
            val ctx = ctxField.get(RetrofitClient) as? android.content.Context
            ctx?.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)?.getBoolean(key, def) ?: def
        } catch (t: Throwable) {
            def
        }
    }

    private fun tryGetPrefInt(key: String, def: Int): Int {
        return try {
            val ctxField = RetrofitClient::class.java.getDeclaredField("context")
            ctxField.isAccessible = true
            val ctx = ctxField.get(RetrofitClient) as? android.content.Context
            ctx?.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)?.getInt(key, def) ?: def
        } catch (t: Throwable) {
            def
        }
    }
}
