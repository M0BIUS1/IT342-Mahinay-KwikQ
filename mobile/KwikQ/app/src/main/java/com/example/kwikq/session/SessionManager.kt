package com.example.kwikq.session

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthSession(token: String, name: String, email: String, role: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .putString(KEY_ROLE, role)
            .putLong(KEY_SESSION_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return !prefs.getString(KEY_TOKEN, null).isNullOrBlank()
    }

    fun getName(): String? = prefs.getString(KEY_NAME, null)

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun getSessionTime(): Long = prefs.getLong(KEY_SESSION_TIME, 0L)

    companion object {
        private const val PREF_NAME = "kwikq_auth"
        private const val KEY_TOKEN = "token"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_SESSION_TIME = "session_time"
    }
}
