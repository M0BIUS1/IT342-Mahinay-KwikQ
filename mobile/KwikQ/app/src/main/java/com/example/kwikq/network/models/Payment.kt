package com.example.kwikq.network.models

import java.util.Date

data class Payment(
    val id: Long,
    val amount: Double,
    val description: String,
    val status: String,
    val createdAt: Date,
    val paidAt: Date?
)
