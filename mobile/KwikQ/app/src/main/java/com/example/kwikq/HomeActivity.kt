package com.example.kwikq

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.session.SessionManager

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome: TextView = findViewById(R.id.tvWelcome)
        val tvSubWelcome: TextView = findViewById(R.id.tvSubWelcome)
        val tvRole: TextView = findViewById(R.id.tvRole)
        val btnLogout: Button = findViewById(R.id.btnLogout)
        val sessionManager = SessionManager(this)

        val userName = intent.getStringExtra("userName") ?: sessionManager.getName()
        val email = intent.getStringExtra("email") ?: sessionManager.getEmail()
        val role = intent.getStringExtra("role") ?: sessionManager.getRole()
        if (!userName.isNullOrBlank()) {
            tvWelcome.text = getString(R.string.welcome_user, userName)
        }
        if (!email.isNullOrBlank()) {
            tvSubWelcome.text = getString(R.string.logged_in_as, email)
        }
        if (!role.isNullOrBlank()) {
            tvRole.text = getString(R.string.role_display, role)
        }

        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
