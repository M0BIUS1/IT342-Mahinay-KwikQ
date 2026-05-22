package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient

class QueueActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var btnRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        container = findViewById(R.id.queueContainer)
        progress = findViewById(R.id.progressQueue)
        btnRefresh = findViewById(R.id.btnQueueRefresh)

        btnRefresh.setOnClickListener { loadQueues() }
        loadQueues()
    }

    private fun loadQueues() {
        progress.visibility = View.VISIBLE
        val call = RetrofitClient.queueApiService.getMyQueues()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.QueueItem>> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.QueueItem>>,
                response: retrofit2.Response<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.QueueItem>>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val items = response.body()?.content ?: emptyList()
                    displayQueues(items)
                } else {
                    displayError("Failed to load queues")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.QueueItem>>, t: Throwable) {
                progress.visibility = View.GONE
                displayError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayQueues(items: List<com.example.kwikq.network.models.QueueItem>) {
        container.removeAllViews()
        if (items.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No queues"
            container.addView(tv)
            return
        }
        items.forEach { it ->
            val tv = TextView(this)
            tv.text = "Book ${it.bookId} — Position: ${it.position}"
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
