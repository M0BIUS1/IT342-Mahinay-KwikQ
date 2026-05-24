package com.example.kwikq.network.models

import java.util.Date

data class Borrowing(
    val id: Long,
    val bookCopyId: Long,
    val userId: Long,
    val borrowedAt: Date,
    val dueDate: Date,
    val returnedAt: Date?
)
