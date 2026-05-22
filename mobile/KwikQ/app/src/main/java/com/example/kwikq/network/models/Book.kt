package com.example.kwikq.network.models

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val category: String?,
    val uniqueCode: String?
)
