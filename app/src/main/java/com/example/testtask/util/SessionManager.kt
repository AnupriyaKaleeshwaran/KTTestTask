package com.example.testtask.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveUser(id: String) {
        prefs.edit().putString("user_id", id).apply()
    }

    fun getUser(): String? = prefs.getString("user_id", null)
}