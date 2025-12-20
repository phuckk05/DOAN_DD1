package com.example.da // Hoặc com.example.da.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
        const val USERNAME = "username"
        const val USER_ROLE = "user_role"
        const val IS_LOGGED_IN = "is_logged_in"
    }

    fun createLoginSession(userId: Int, username: String, role: String) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USERNAME, username)
        editor.putString(USER_ROLE, role)
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply()
    }

    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun getUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }
}
