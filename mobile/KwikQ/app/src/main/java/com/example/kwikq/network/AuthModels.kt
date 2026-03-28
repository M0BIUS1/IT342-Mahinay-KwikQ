package com.example.kwikq.network

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val type: String,
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)

data class MessageResponse(
    val message: String
)
