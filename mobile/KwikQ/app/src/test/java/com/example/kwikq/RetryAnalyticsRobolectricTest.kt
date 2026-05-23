package com.example.kwikq

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.example.kwikq.network.RetryAnalytics

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class RetryAnalyticsRobolectricTest {

    @Test
    fun sendEvent_respects_sampling_and_calls_hook_when_enabled() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        // initialize RetrofitClient context indirectly via reflection to keep test isolated
        // use SharedPreferences to enable analytics and set sample to 100
        val prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("analytics_enabled", true).putInt("analytics_sample_percent", 100).commit()

        var called = false
        RetryAnalytics.analyticsSender = { payload ->
            called = true
            // basic assertions
            assertTrue(payload.containsKey("app"))
            assertTrue(payload.containsKey("ts"))
        }

        RetryAnalytics.sendEventIfEnabled(mapOf("action" to "retry_test"))

        // cleanup
        RetryAnalytics.analyticsSender = null

        assertTrue(called)
    }
}
