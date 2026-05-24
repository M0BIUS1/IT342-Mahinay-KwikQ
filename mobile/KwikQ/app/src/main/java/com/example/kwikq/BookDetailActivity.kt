package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kwikq.adapters.CopyAdapter
import com.example.kwikq.network.NetworkUtils
import com.example.kwikq.network.RetrofitClient
import com.example.kwikq.network.models.BookCopyItem

class BookDetailActivity : AppCompatActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var progress: ProgressBar
    private lateinit var rvCopies: RecyclerView
    private lateinit var adapter: CopyAdapter
    private var bookId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)
        tvTitle = findViewById(R.id.tvBookTitle)
        progress = findViewById(R.id.progressCopies)
        rvCopies = findViewById(R.id.rvCopies)

        adapter = CopyAdapter(mutableListOf(), object : CopyAdapter.OnCopyActionListener {
            override fun onBorrow(item: BookCopyItem) {
                AlertDialog.Builder(this@BookDetailActivity)
                    .setTitle("Borrow")
                    .setMessage("Borrow copy ${item.copyCode}?")
                    .setPositiveButton("Yes") { _, _ -> performBorrow(item) }
                    .setNegativeButton("No", null)
                    .show()
            }
        })

        rvCopies.layoutManager = LinearLayoutManager(this)
        rvCopies.adapter = adapter

        bookId = intent.getLongExtra("bookId", -1L)
        val title = intent.getStringExtra("bookTitle") ?: ""
        tvTitle.text = title
        if (bookId > 0) loadCopies(bookId)
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

    private fun loadCopies(bookId: Long) {
        progress.visibility = View.VISIBLE
        adapter.setEnabled(false)
        val callFactory = { RetrofitClient.bookApiService.getBookCopies(bookId) }
        NetworkUtils.enqueueWithRetry(callFactory, 3, 400, description = "LoadCopies:$bookId", onRetry = { attempt ->
            Toast.makeText(this@BookDetailActivity, "Retrying load copies (attempt $attempt)", Toast.LENGTH_SHORT).show()
        }, callback = object : retrofit2.Callback<List<BookCopyItem>> {
            override fun onResponse(call: retrofit2.Call<List<BookCopyItem>>, response: retrofit2.Response<List<BookCopyItem>>) {
                progress.visibility = View.GONE
                adapter.setEnabled(true)
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    adapter.update(list)
                    findViewById<TextView>(R.id.tvCopiesEmpty)?.let { emptyTv ->
                        emptyTv.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }
                } else {
                    Toast.makeText(this@BookDetailActivity, "Failed to load copies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<BookCopyItem>>, t: Throwable) {
                progress.visibility = View.GONE
                adapter.setEnabled(true)
                Toast.makeText(this@BookDetailActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performBorrow(item: BookCopyItem) {
        progress.visibility = View.VISIBLE
        adapter.setEnabled(false)
        val callFactory = { RetrofitClient.borrowingApiService.borrowBook(item.id) }
        NetworkUtils.enqueueWithRetry(callFactory, 3, 400, description = "BorrowCopy:${item.id}", onRetry = { attempt ->
            Toast.makeText(this@BookDetailActivity, "Retrying borrow (attempt $attempt)", Toast.LENGTH_SHORT).show()
        }, callback = object : retrofit2.Callback<com.example.kwikq.network.MessageResponse> {
            override fun onResponse(call: retrofit2.Call<com.example.kwikq.network.MessageResponse>, response: retrofit2.Response<com.example.kwikq.network.MessageResponse>) {
                progress.visibility = View.GONE
                adapter.setEnabled(true)
                if (response.isSuccessful) {
                    Toast.makeText(this@BookDetailActivity, response.body()?.message ?: "Borrowed", Toast.LENGTH_SHORT).show()
                    if (bookId > 0) loadCopies(bookId)
                } else {
                    Toast.makeText(this@BookDetailActivity, "Failed to borrow", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.MessageResponse>, t: Throwable) {
                progress.visibility = View.GONE
                Toast.makeText(this@BookDetailActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
