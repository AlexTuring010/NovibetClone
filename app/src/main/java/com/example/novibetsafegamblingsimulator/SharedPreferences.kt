package com.example.novibetsafegamblingsimulator

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        editor.putInt("customer_id", user.customer_id)
        editor.putString("username", user.username)
        editor.putFloat("total_remain", user.total_remain)
        editor.putInt("age_band", user.age_band)
        editor.putInt("gender", user.gender)
        editor.putString("password", user.password)
        editor.apply()
    }

    fun saveDate(date: String) {
        val editor = sharedPreferences.edit()
        editor.putString("date", date)
        editor.apply()
    }

    fun getUser(): User? {
        val customerId = sharedPreferences.getInt("customer_id", -1)
        if (customerId == -1) return null

        val username = sharedPreferences.getString("username", null) ?: return null
        val totalRemain = sharedPreferences.getFloat("total_remain", -1f).toFloat()
        val ageBand = sharedPreferences.getInt("age_band", -1)
        val gender = sharedPreferences.getInt("gender", -1)
        val password = sharedPreferences.getString("password", null) ?: return null

        return User(customerId, ageBand, gender, totalRemain, username, password)
    }

    fun getDate(): String? {
        val date = sharedPreferences.getString("date", null) ?: return null
        return date
    }

    fun clearUser() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}