package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient

class PaymentsActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)
        container = findViewById(R.id.paymentsContainer)
        progress = findViewById(R.id.progressPayments)
        btnRefresh = findViewById(R.id.btnPaymentsRefresh)

        btnRefresh.setOnClickListener { loadPayments() }
        loadPayments()
    }

    private fun loadPayments() {
        progress.visibility = View.VISIBLE
        val call = RetrofitClient.paymentsApiService.getMyPayments()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.PaymentResponse>> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.PaymentResponse>>,
                response: retrofit2.Response<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.PaymentResponse>>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val items = response.body()?.content ?: emptyList()
                    displayPayments(items)
                } else {
                    displayError("Failed to load payments")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.PaymentResponse>>, t: Throwable) {
                progress.visibility = View.GONE
                displayError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayPayments(items: List<com.example.kwikq.network.models.PaymentResponse>) {
        container.removeAllViews()
        if (items.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No payments"
            container.addView(tv)
            return
        }
        items.forEach { p ->
            val tv = TextView(this)
            tv.text = "$${p.amount} — ${p.description} — ${p.status}"
            tv.setPadding(8,12,8,12)
            container.addView(tv)
        }
    }

    private fun displayError(msg: String) {
        container.removeAllViews()
        val tv = TextView(this)
        tv.text = msg
        container.addView(tv)
    }
}
