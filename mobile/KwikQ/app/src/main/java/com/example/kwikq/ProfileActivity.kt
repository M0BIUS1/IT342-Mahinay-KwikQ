package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient

class ProfileActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        container = findViewById(R.id.profileContainer)
        progress = findViewById(R.id.progressProfile)

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
