package me.integrate.socialbank

import android.content.Context

object SharedPreferencesManager {

    private const val PREFERENCES_FILE: String = "me.integrate.socialbank.PREFERENCE_FILE_KEY"

    fun store(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun read(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    fun remove(context: Context, key: String) {
        val sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
                ?: return
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }
}