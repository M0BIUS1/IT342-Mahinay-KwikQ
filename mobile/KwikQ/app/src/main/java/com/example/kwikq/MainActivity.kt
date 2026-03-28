package com.example.kwikq

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.kwikq.session.SessionManager
import com.example.kwikq.supabase.SupabaseClientManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressLogin: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToRegister: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("userName", sessionManager.getName())
            intent.putExtra("email", sessionManager.getEmail())
            intent.putExtra("role", sessionManager.getRole())
            startActivity(intent)
            finish()
            return
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressLogin = findViewById(R.id.progressLogin)
        tvStatus = findViewById(R.id.tvStatus)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        val prefillEmail = intent.getStringExtra("email")
        if (!prefillEmail.isNullOrBlank()) {
            etEmail.setText(prefillEmail)
        }

        btnLogin.setOnClickListener {
            submitLogin()
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun submitLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        etEmail.error = null
        etPassword.error = null

        val validationError = validateInput(email, password)
        if (validationError != null) {
            showStatus(validationError, true)
            return
        }

        showLoading(true)
        tvStatus.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = SupabaseClientManager.signIn(email, password)

                if (response != null && response.access_token != null) {
                    val userName = email.substringBefore("@")
                    sessionManager.saveAuthSession(response.access_token, userName, email, "user")
                    
                    // Show success confirmation
                    android.widget.Toast.makeText(this@MainActivity, "✓ Login successful! Welcome back, $userName!", android.widget.Toast.LENGTH_LONG).show()
                    
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    intent.putExtra("userName", userName)
                    intent.putExtra("email", email)
                    intent.putExtra("role", "user")
                    startActivity(intent)
                    finish()
                } else {
                    showStatus(getString(R.string.network_error, "Login failed"), true)
                }
            } catch (e: Exception) {
                showLoading(false)
                showStatus(getString(R.string.network_error, e.localizedMessage ?: "Unknown error"), true)
            }
        }
    }

    private fun validateInput(email: String, password: String): String? {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.validation_email)
            return getString(R.string.validation_email)
        }
        if (password.isBlank()) {
            etPassword.error = getString(R.string.validation_password_required)
            return getString(R.string.validation_password_required)
        }
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        progressLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
        btnLogin.text = getString(if (isLoading) R.string.login_loading else R.string.login)
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        tvGoToRegister.isEnabled = !isLoading
    }

    private fun showStatus(message: String, isError: Boolean) {
        showLoading(false)
        tvStatus.text = message
        tvStatus.setTextColor(
            ContextCompat.getColor(this, if (isError) R.color.kw_danger else R.color.kw_accent)
        )
        tvStatus.visibility = View.VISIBLE
    }
}