package com.example.kwikq.network.models

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val bio: String?,
    val pictureUrl: String?
)
