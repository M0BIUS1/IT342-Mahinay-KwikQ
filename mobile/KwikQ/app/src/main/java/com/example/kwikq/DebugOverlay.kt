package com.example.kwikq

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.kwikq.network.RetryLogger

object DebugOverlay {
    private const val TAG = "DebugOverlay"

    private val overlays = mutableMapOf<Activity, TextView>()

    fun attach(activity: Activity) {
        val prefs = activity.getSharedPreferences("app_prefs", Activity.MODE_PRIVATE)
        val enabled = prefs.getBoolean("debug_overlay_enabled", false)
        if (!enabled) return
        if (overlays.containsKey(activity)) return
        val decor = activity.window.decorView as ViewGroup
        val tv = TextView(activity).apply {
            setBackgroundColor(Color.parseColor("#80000000"))
            setTextColor(Color.WHITE)
            textSize = 12f
            setPadding(12, 8, 12, 8)
            val count = RetryLogger.getEvents().size
            text = "Retries: $count"
        }

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END or Gravity.TOP)
        params.setMargins(16, 16, 16, 16)
        decor.addView(tv, params)
        overlays[activity] = tv
    }

    fun detach(activity: Activity) {
        overlays.remove(activity)?.let { tv ->
            val decor = activity.window.decorView as ViewGroup
            decor.removeView(tv)
        }
    }

    fun refresh(activity: Activity) {
        overlays[activity]?.let { tv ->
            tv.text = "Retries: ${RetryLogger.getEvents().size}"
        }
    }
}
