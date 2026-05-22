package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient

class BorrowingActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrowing)
        container = findViewById(R.id.borrowingContainer)
        progress = findViewById(R.id.progressBorrowing)

        loadHistory()
    }

    private fun loadHistory() {
        progress.visibility = View.VISIBLE
        val call = RetrofitClient.borrowingApiService.getBorrowingHistory()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Borrowing>> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Borrowing>>,
                response: retrofit2.Response<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Borrowing>>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val items = response.body()?.content ?: emptyList()
                    displayHistory(items)
                } else {
                    displayError("Failed to load borrowing history")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Borrowing>>, t: Throwable) {
                progress.visibility = View.GONE
                displayError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayHistory(items: List<com.example.kwikq.network.models.Borrowing>) {
        container.removeAllViews()
        if (items.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No borrowing history"
            container.addView(tv)
            return
        }
        items.forEach { b ->
            val tv = TextView(this)
            tv.text = "Copy ${b.bookCopyId} — Borrowed: ${b.borrowedAt}"
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
