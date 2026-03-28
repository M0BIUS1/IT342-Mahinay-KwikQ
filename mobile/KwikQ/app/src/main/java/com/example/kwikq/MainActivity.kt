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
import com.example.kwikq.network.ApiErrorParser
import com.example.kwikq.network.AuthResponse
import com.example.kwikq.network.LoginRequest
import com.example.kwikq.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressLogin: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        val validationError = validateInput(email, password)
        if (validationError != null) {
            showStatus(validationError)
            return
        }

        showLoading(true)
        tvStatus.visibility = View.GONE

        RetrofitClient.authApiService.login(LoginRequest(email, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    showLoading(false)

                    if (response.isSuccessful && response.body() != null) {
                        val auth = response.body()!!
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        intent.putExtra("userName", auth.name)
                        startActivity(intent)
                        finish()
                    } else {
                        showStatus(ApiErrorParser.getMessage(response))
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    showLoading(false)
                    showStatus(getString(R.string.network_error, t.localizedMessage ?: "Unknown error"))
                }
            })
    }

    private fun validateInput(email: String, password: String): String? {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return getString(R.string.validation_email)
        if (password.isBlank()) return getString(R.string.validation_password_required)
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        progressLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
    }

    private fun showStatus(message: String) {
        tvStatus.text = message
        tvStatus.visibility = View.VISIBLE
    }
}