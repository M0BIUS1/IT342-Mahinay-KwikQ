package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient
import android.content.Context
import android.widget.Switch
import android.widget.Toast
import android.widget.Button
import com.example.kwikq.DebugOverlay

class ProfileActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        container = findViewById(R.id.profileContainer)
        progress = findViewById(R.id.progressProfile)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val switch = findViewById<Switch>(R.id.switchDebugOverlay)
        val enabled = prefs.getBoolean("debug_overlay_enabled", false)
        switch.isChecked = enabled
        switch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("debug_overlay_enabled", isChecked).apply()
            if (isChecked) {
                DebugOverlay.attach(this)
                DebugOverlay.refresh(this)
                Toast.makeText(this, "Debug overlay enabled", Toast.LENGTH_SHORT).show()
            } else {
                DebugOverlay.detach(this)
                Toast.makeText(this, "Debug overlay disabled", Toast.LENGTH_SHORT).show()
            }
        }

        // Analytics controls
        val switchAnalytics = findViewById<Switch>(R.id.switchAnalytics)
        val etSampling = findViewById<android.widget.EditText>(R.id.etSamplingPercent)
        val analyticsEnabled = prefs.getBoolean("analytics_enabled", false)
        val sampling = prefs.getInt("analytics_sample_percent", 10)
        switchAnalytics.isChecked = analyticsEnabled
        etSampling.setText(sampling.toString())
        switchAnalytics.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("analytics_enabled", isChecked).apply()
            Toast.makeText(this, if (isChecked) "Analytics enabled" else "Analytics disabled", Toast.LENGTH_SHORT).show()
        }
        val btnSaveSampling = findViewById<Button>(R.id.btnSaveSampling)
        val saveSampling = {
            val v = etSampling.text.toString().toIntOrNull() ?: 10
            val s = v.coerceIn(0, 100)
            prefs.edit().putInt("analytics_sample_percent", s).apply()
            Toast.makeText(this, "Sampling set to $s%", Toast.LENGTH_SHORT).show()
        }
        btnSaveSampling.setOnClickListener { saveSampling() }
        btnSaveSampling.setOnLongClickListener {
            saveSampling()
            true
        }

        loadProfile()
    }

    private fun loadProfile() {
        progress.visibility = View.VISIBLE
        val call = RetrofitClient.profileApiService.getProfile()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.models.UserProfile> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.models.UserProfile>,
                response: retrofit2.Response<com.example.kwikq.network.models.UserProfile>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    displayProfile(response.body()!!)
                } else {
                    displayError("Failed to load profile")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.models.UserProfile>, t: Throwable) {
                progress.visibility = View.GONE
                displayError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayProfile(p: com.example.kwikq.network.models.UserProfile) {
        container.removeAllViews()
        val tvName = TextView(this)
        tvName.text = "Name: ${p.name}"
        val tvEmail = TextView(this)
        tvEmail.text = "Email: ${p.email}"
        container.addView(tvName)
        container.addView(tvEmail)
    }

    private fun displayError(msg: String) {
        container.removeAllViews()
        val tv = TextView(this)
        tv.text = msg
        container.addView(tv)
    }
}
