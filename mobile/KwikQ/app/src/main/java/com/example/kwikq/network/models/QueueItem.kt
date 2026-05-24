package com.example.kwikq.network.models

data class QueueItem(
    val id: Long,
    val bookId: Long,
    val userId: Long,
    val position: Int,
    val status: String
)
