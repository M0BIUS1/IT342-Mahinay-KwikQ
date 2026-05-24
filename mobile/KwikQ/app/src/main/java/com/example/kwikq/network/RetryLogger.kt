package com.example.kwikq.network

import android.util.Log

object RetryLogger {
    private const val TAG = "NetworkRetry"
    private val events = mutableListOf<String>()

    @Synchronized
    fun log(attempt: Int, action: String) {
        val msg = "attempt=$attempt action=$action"
        events.add(msg)
        try {
            Log.i(TAG, msg)
        } catch (e: Throwable) {
            // ignore logging errors in non-Android test environments
        }
        // Attempt to export analytics (non-blocking)
        try {
            RetryAnalytics.sendEventIfEnabled(mapOf("attempt" to attempt, "action" to action, "message" to msg))
        } catch (ignored: Throwable) {
        }
    }

    @Synchronized
    fun getEvents(): List<String> = events.toList()

    @Synchronized
    fun clear() = events.clear()
}
