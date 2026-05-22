package com.example.kwikq

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        findViewById<Button>(R.id.btnBooks).setOnClickListener {
            startActivity(Intent(this, BooksActivity::class.java))
        }
        findViewById<Button>(R.id.btnQueues).setOnClickListener {
            startActivity(Intent(this, QueueActivity::class.java))
        }
        findViewById<Button>(R.id.btnBorrowings).setOnClickListener {
            startActivity(Intent(this, BorrowingActivity::class.java))
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<Button>(R.id.btnPayments).setOnClickListener {
            startActivity(Intent(this, PaymentsActivity::class.java))
        }
    }
}
