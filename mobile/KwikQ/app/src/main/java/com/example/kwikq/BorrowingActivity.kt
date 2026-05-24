package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.NetworkUtils
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

    override fun onResume() {
        super.onResume()
        DebugOverlay.attach(this)
        DebugOverlay.refresh(this)
    }

    override fun onPause() {
        super.onPause()
        DebugOverlay.detach(this)
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
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(8,12,8,12)

            val tv = TextView(this)
            tv.text = "Copy ${b.bookCopyId} — Borrowed: ${b.borrowedAt}"
            tv.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            row.addView(tv)

            if (b.returnedAt == null) {
                val btnReturn = Button(this)
                btnReturn.text = "Return"
                btnReturn.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Return book")
                        .setMessage("Return copy ${b.bookCopyId}?")
                        .setPositiveButton("Yes") { _, _ -> performReturn(b.id, btnReturn) }
                        .setNegativeButton("No", null)
                        .show()
                }
                row.addView(btnReturn)
            }

            container.addView(row)
        }
    }

    private fun performReturn(borrowingId: Long, btn: Button) {
        btn.isEnabled = false
        progress.visibility = View.VISIBLE
        val callFactory = { RetrofitClient.borrowingApiService.returnBook(borrowingId) }
        NetworkUtils.enqueueWithRetry(callFactory, 3, 400, description = "ReturnBorrowing:$borrowingId", onRetry = { attempt ->
            Toast.makeText(this@BorrowingActivity, "Retrying return (attempt $attempt)", Toast.LENGTH_SHORT).show()
        }, callback = object : retrofit2.Callback<com.example.kwikq.network.MessageResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.MessageResponse>,
                response: retrofit2.Response<com.example.kwikq.network.MessageResponse>
            ) {
                progress.visibility = View.GONE
                btn.isEnabled = true
                if (response.isSuccessful) {
                    Toast.makeText(this@BorrowingActivity, response.body()?.message ?: "Returned", Toast.LENGTH_SHORT).show()
                    loadHistory()
                } else {
                    Toast.makeText(this@BorrowingActivity, "Failed to return", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.MessageResponse>, t: Throwable) {
                progress.visibility = View.GONE
                btn.isEnabled = true
                Toast.makeText(this@BorrowingActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayError(msg: String) {
        container.removeAllViews()
        val tv = TextView(this)
        tv.text = msg
        container.addView(tv)
    }
}
