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
import com.example.kwikq.network.RetryLogger

class BooksActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var tvRetryCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        container = findViewById(R.id.booksContainer)
        progress = findViewById(R.id.progressBooks)
        tvRetryCount = findViewById(R.id.tvRetryCount)
        findViewById<Button>(R.id.btnRetryLog).setOnClickListener {
            startActivity(android.content.Intent(this, RetryLogActivity::class.java))
        }

        loadBooks()
    }

    override fun onResume() {
        super.onResume()
        tvRetryCount.text = "Retries: ${RetryLogger.getEvents().size}"
        DebugOverlay.attach(this)
        DebugOverlay.refresh(this)
    }

    override fun onPause() {
        super.onPause()
        DebugOverlay.detach(this)
    }

    private fun loadBooks() {
        progress.visibility = View.VISIBLE
        container.alpha = 0.6f
        val call = RetrofitClient.bookApiService.getBooks()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>,
                response: retrofit2.Response<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>
            ) {
                progress.visibility = View.GONE
                container.alpha = 1.0f
                if (response.isSuccessful) {
                    val books = response.body()?.content ?: emptyList()
                    displayBooks(books)
                } else {
                    displayError("Failed to load books")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>, t: Throwable) {
                progress.visibility = View.GONE
                container.alpha = 1.0f
                displayError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayBooks(books: List<com.example.kwikq.network.models.Book>) {
        container.removeAllViews()
        if (books.isEmpty()) {
            val tv = TextView(this)
            tv.text = "No books found"
            container.addView(tv)
            return
        }
        books.forEach { book ->
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(8, 12, 8, 12)

            val tv = TextView(this)
                tv.text = "${book.title} — ${book.author}"
                tv.setOnClickListener {
                    val intent = android.content.Intent(this, BookDetailActivity::class.java)
                    intent.putExtra("bookId", book.id)
                    intent.putExtra("bookTitle", book.title)
                    startActivity(intent)
                }
            tv.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val btn = Button(this)
            btn.text = "Add to queue"
            btn.setOnClickListener {
                // confirmation
                AlertDialog.Builder(this)
                    .setTitle("Add to queue")
                    .setMessage("Add '${book.title}' to your queue?")
                    .setPositiveButton("Yes") { _, _ -> performAddToQueue(book.id, btn) }
                    .setNegativeButton("No", null)
                    .show()
            }

            row.addView(tv)
            row.addView(btn)
            container.addView(row)
        }
    }

    private fun performAddToQueue(bookId: Long, btn: Button) {
        btn.isEnabled = false
        btn.alpha = 0.5f
        progress.visibility = View.VISIBLE
        val callFactory = { RetrofitClient.queueApiService.addToQueue(bookId) }
        NetworkUtils.enqueueWithRetry(callFactory, 3, 400, description = "AddToQueue:$bookId", onRetry = { attempt ->
            Toast.makeText(this@BooksActivity, "Retrying add-to-queue (attempt $attempt)", Toast.LENGTH_SHORT).show()
        }, callback = object : retrofit2.Callback<com.example.kwikq.network.MessageResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.MessageResponse>,
                response: retrofit2.Response<com.example.kwikq.network.MessageResponse>
            ) {
                progress.visibility = View.GONE
                btn.isEnabled = true
                btn.alpha = 1.0f
                if (response.isSuccessful) {
                    Toast.makeText(this@BooksActivity, response.body()?.message ?: "Added to queue", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BooksActivity, "Failed to add to queue", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.MessageResponse>, t: Throwable) {
                progress.visibility = View.GONE
                btn.isEnabled = true
                Toast.makeText(this@BooksActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
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
