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
import com.example.kwikq.session.SessionManager
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
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressRegister: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvGoToLogin: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressRegister = findViewById(R.id.progressRegister)
        tvStatus = findViewById(R.id.tvStatus)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
        sessionManager = SessionManager(this)

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
        val confirmPassword = etConfirmPassword.text.toString()

        etName.error = null
        etEmail.error = null
        etPassword.error = null
        etConfirmPassword.error = null

        val validationError = validateInput(name, email, password, confirmPassword)
        if (validationError != null) {
            showStatus(validationError, true)
            return
        }

        showLoading(true)
        tvStatus.visibility = View.GONE

        RetrofitClient.authApiService.register(RegisterRequest(name, email, password))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    showLoading(false)

                    if (response.isSuccessful && response.body() != null) {
                        val auth = response.body()!!
                        sessionManager.saveAuthSession(auth.token, auth.name, auth.email, auth.role)

                        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                        intent.putExtra("userName", auth.name)
                        intent.putExtra("email", auth.email)
                        intent.putExtra("role", auth.role)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showStatus(ApiErrorParser.getMessage(response), true)
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    showLoading(false)
                    showStatus(getString(R.string.network_error, t.localizedMessage ?: "Unknown error"), true)
                }
            })
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): String? {
        if (name.length < 2) {
            etName.error = getString(R.string.validation_name)
            return getString(R.string.validation_name)
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.validation_email)
            return getString(R.string.validation_email)
        }
        if (password.length < 6) {
            etPassword.error = getString(R.string.validation_password)
            return getString(R.string.validation_password)
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = getString(R.string.validation_password_mismatch)
            return getString(R.string.validation_password_mismatch)
        }
        return null
    }

    private fun showLoading(isLoading: Boolean) {
        progressRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !isLoading
        btnRegister.text = getString(if (isLoading) R.string.register_loading else R.string.register)
        etName.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etConfirmPassword.isEnabled = !isLoading
        tvGoToLogin.isEnabled = !isLoading
    }

    private fun showStatus(message: String, isError: Boolean) {
        tvStatus.text = message
        tvStatus.setTextColor(
            ContextCompat.getColor(this, if (isError) R.color.kw_danger else R.color.kw_accent)
        )
        tvStatus.visibility = View.VISIBLE
    }
}
