package com.example.kwikq

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kwikq.network.ApiErrorParser
import com.example.kwikq.network.AuthResponse
import com.example.kwikq.network.RegisterRequest
import com.example.kwikq.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressRegister: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressRegister = findViewById(R.id.progressRegister)
        tvStatus = findViewById(R.id.tvStatus)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        btnRegister.setOnClickListener {
            submitRegistration()
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun submitRegistration() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        val validationError = validateInput(name, email, password)
        if (validationError != null) {
            showStatus(validationError)
            return
        }

        showLoading(true)
        tvStatus.visibility = View.GONE

        RetrofitClient.authApiService.register(RegisterRequest(name, email, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    showLoading(false)

                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.putExtra("email", email)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    private fun validateInput(name: String, email: String, password: String): String? {
        if (name.length < 2) return getString(R.string.validation_name)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return getString(R.string.validation_email)
        if (password.length < 6) return getString(R.string.validation_password)
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
    }

    private fun showStatus(message: String) {
        tvStatus.text = message
        tvStatus.visibility = View.VISIBLE
    }
}
