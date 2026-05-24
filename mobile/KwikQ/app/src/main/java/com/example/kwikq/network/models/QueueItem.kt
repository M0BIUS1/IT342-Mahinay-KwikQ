package com.example.kwikq.network.models

data class QueueItem(
    val id: Long,
    val bookId: Long,
    val userId: Long? = null,
    // support both `position` and `queuePosition` coming from server
    val position: Int? = null,
    val queuePosition: Int? = null,
    val status: String? = null,

    // optional fields added by backend: book title/author and user metadata
    val bookTitle: String? = null,
    val bookAuthor: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,

    // timestamp fields: some APIs use `queuedAt`, others `requestedAt`
    val queuedAt: String? = null,
    val requestedAt: String? = null
)
