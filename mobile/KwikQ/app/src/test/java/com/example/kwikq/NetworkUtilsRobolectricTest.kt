package com.example.kwikq

import com.example.kwikq.network.NetworkUtils
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicInteger
import okio.Timeout

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class NetworkUtilsRobolectricTest {

    @Test
    fun retryAttempts_happen_and_final_success_calls_onResponse() {
        val attempts = AtomicInteger(0)
        val recordedRetries = mutableListOf<Int>()
        var responseCalled = false
        var failureCalled = false

        val callFactory = {
            object : Call<Void> {
                override fun enqueue(callback: Callback<Void>) {
                    val a = attempts.getAndIncrement()
                    if (a < 2) {
                        callback.onFailure(this, Throwable("simulated"))
                    } else {
                        callback.onResponse(this, Response.success(null))
                    }
                }

                override fun isExecuted(): Boolean = false
                override fun clone(): Call<Void> = this
                override fun isCanceled(): Boolean = false
                override fun cancel() {}
                override fun execute(): Response<Void> = Response.success(null)
                override fun request(): Request = Request.Builder().url("http://localhost/").build()
                override fun timeout(): Timeout = Timeout.NONE
            }
        }

        val scheduler: (Long, () -> Unit) -> Unit = { _, r -> r() }

        val callback = object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                responseCalled = true
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                failureCalled = true
            }
        }

        NetworkUtils.enqueueWithRetry(callFactory, attempts = 3, baseDelayMs = 1, description = "test", onRetry = { attempt ->
            recordedRetries.add(attempt)
        }, scheduler = scheduler, callback = callback)

        // since scheduler executes immediately and fake calls are synchronous, flow is synchronous
        assertTrue(responseCalled)
        assertTrue(recordedRetries.size >= 2)
        assertEquals(listOf(2, 3), recordedRetries)
        assertEquals(3, attempts.get())
    }
}
