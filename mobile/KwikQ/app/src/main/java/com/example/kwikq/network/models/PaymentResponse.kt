package com.example.kwikq.network.models

import java.util.Date

data class PaymentResponse(
    val id: Long,
    val amount: Double,
    val description: String,
    val status: String,
    val createdAt: Date,
    val paidAt: Date?
)
