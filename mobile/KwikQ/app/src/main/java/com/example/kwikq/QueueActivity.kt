package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kwikq.adapters.QueueAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.NetworkUtils
import com.example.kwikq.network.RetrofitClient

class QueueActivity : AppCompatActivity() {
    private lateinit var rvQueues: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var btnRefresh: Button
    private lateinit var adapter: QueueAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        rvQueues = findViewById(R.id.rvQueues)
        progress = findViewById(R.id.progressQueue)
        btnRefresh = findViewById(R.id.btnQueueRefresh)

        adapter = QueueAdapter(mutableListOf(), object : com.example.kwikq.adapters.QueueAdapter.OnQueueActionListener {
            override fun onRemove(item: com.example.kwikq.network.models.QueueItem) {
                performRemove(item)
            }
        })
        rvQueues.layoutManager = LinearLayoutManager(this)
        rvQueues.adapter = adapter

        btnRefresh.setOnClickListener { loadQueues() }
        loadQueues()
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
                    adapter.update(items)
                } else {
                    Toast.makeText(this@QueueActivity, "Failed to load queues", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.QueueItem>>, t: Throwable) {
                progress.visibility = View.GONE
                Toast.makeText(this@QueueActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // queue list is rendered by RecyclerView adapter

    private fun performRemove(item: com.example.kwikq.network.models.QueueItem) {
        progress.visibility = View.VISIBLE
        adapter.setEnabled(false)
        val callFactory = { RetrofitClient.queueApiService.removeFromQueue(item.id) }
        NetworkUtils.enqueueWithRetry(callFactory, 3, 400, description = "RemoveQueue:${item.id}", onRetry = { attempt ->
            Toast.makeText(this@QueueActivity, "Retrying remove from queue (attempt $attempt)", Toast.LENGTH_SHORT).show()
        }, callback = object : retrofit2.Callback<com.example.kwikq.network.MessageResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.MessageResponse>,
                response: retrofit2.Response<com.example.kwikq.network.MessageResponse>
            ) {
                progress.visibility = View.GONE
                adapter.setEnabled(true)
                if (response.isSuccessful) {
                    adapter.remove(item)
                    Toast.makeText(this@QueueActivity, response.body()?.message ?: "Removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@QueueActivity, "Failed to remove from queue", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.MessageResponse>, t: Throwable) {
                progress.visibility = View.GONE
                adapter.setEnabled(true)
                Toast.makeText(this@QueueActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
