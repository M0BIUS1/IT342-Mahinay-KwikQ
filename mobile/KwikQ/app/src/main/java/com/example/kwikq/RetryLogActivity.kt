package com.example.kwikq

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.RetryLogger

class RetryLogActivity : AppCompatActivity() {
    private lateinit var tvEvents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retry_log)
        tvEvents = findViewById(R.id.tvRetryEvents)

        findViewById<Button>(R.id.btnRefreshRetries).setOnClickListener { refresh() }
        findViewById<Button>(R.id.btnClearRetries).setOnClickListener {
            RetryLogger.clear()
            refresh()
        }
        findViewById<Button>(R.id.btnCopyRetries).setOnClickListener {
            val events = RetryLogger.getEvents().joinToString(separator = "\n")
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("retry_events", events)
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(this, "Copied ${RetryLogger.getEvents().size} events to clipboard", android.widget.Toast.LENGTH_SHORT).show()
        }

        refresh()
    }

    private fun refresh() {
        val events = RetryLogger.getEvents()
        if (events.isEmpty()) {
            tvEvents.text = "(no events)"
        } else {
            tvEvents.text = events.joinToString(separator = "\n")
        }
    }
}
