package com.example.kwikq.network

import com.google.gson.Gson
import retrofit2.Response

object ApiErrorParser {
    fun getMessage(response: Response<*>): String {
        return try {
            val raw = response.errorBody()?.string()
            if (raw.isNullOrBlank()) {
                "Request failed (${response.code()})"
            } else {
                Gson().fromJson(raw, MessageResponse::class.java)?.message
                    ?: "Request failed (${response.code()})"
            }
        } catch (_: Exception) {
            "Request failed (${response.code()})"
        }
    }
}
