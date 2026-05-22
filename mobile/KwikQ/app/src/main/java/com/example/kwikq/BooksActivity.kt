package com.example.kwikq

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetrofitClient

class BooksActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        container = findViewById(R.id.booksContainer)
        progress = findViewById(R.id.progressBooks)

        loadBooks()
    }

    private fun loadBooks() {
        progress.visibility = View.VISIBLE
        val call = RetrofitClient.bookApiService.getBooks()
        call.enqueue(object : retrofit2.Callback<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>> {
            override fun onResponse(
                call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>,
                response: retrofit2.Response<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>
            ) {
                progress.visibility = View.GONE
                if (response.isSuccessful) {
                    val books = response.body()?.content ?: emptyList()
                    displayBooks(books)
                } else {
                    displayError("Failed to load books")
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.kwikq.network.PagedResponse<com.example.kwikq.network.models.Book>>, t: Throwable) {
                progress.visibility = View.GONE
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
            val tv = TextView(this)
            tv.text = "${book.title} — ${book.author}"
            tv.textSize = 16f
            tv.setPadding(8, 12, 8, 12)
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
