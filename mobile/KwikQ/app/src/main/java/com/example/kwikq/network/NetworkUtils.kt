package com.example.kwikq.network

import android.os.Handler
import android.os.Looper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NetworkUtils {
    fun <T> enqueueWithRetry(
        callFactory: () -> Call<T>,
        attempts: Int = 3,
        baseDelayMs: Long = 400,
        description: String? = null,
        onRetry: ((attempt: Int) -> Unit)? = null,
        // scheduler: accepts (delayMs, runnable) -> Unit, useful for tests
        scheduler: (Long, () -> Unit) -> Unit = { delayMs, runnable ->
            Handler(Looper.getMainLooper()).postDelayed(runnable, delayMs)
        },
        callback: Callback<T>
    ) {

        fun tryOnce(remaining: Int, attemptNumber: Int) {
            val call = callFactory()
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callback.onResponse(call, response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (remaining > 1) {
                        val nextAttempt = attemptNumber + 1
                        // record/log retry
                        RetryLogger.log(nextAttempt, description ?: "retry")
                        onRetry?.invoke(nextAttempt)
                        val delay = baseDelayMs * (1L shl (attemptNumber - 1))
                        scheduler(delay) { tryOnce(remaining - 1, nextAttempt) }
                    } else {
                        callback.onFailure(call, t)
                    }
                }
            })
        }

        tryOnce(attempts, 1)
    }
}
