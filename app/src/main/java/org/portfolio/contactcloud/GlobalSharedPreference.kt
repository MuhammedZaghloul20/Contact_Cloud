package org.portfolio.contactcloud

import android.content.Context
import android.content.SharedPreferences

class GlobalSharedPreference (context:Context){


    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    companion object {
        private var instance: GlobalSharedPreference? = null

        fun getInstance(context: Context): GlobalSharedPreference {
            if (instance == null) {
                instance = GlobalSharedPreference(context)
            }
            return instance as GlobalSharedPreference
        }
    }

    fun setValue(key: String, value: Any?) {
        val editor = prefs.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    inline fun <reified T : Any> getValue(key: String, defaultValue: T? = null): T? {
        return when (T::class) {
            String::class -> prefs.getString(key, defaultValue as? String) as T?
            Int::class -> prefs.getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> prefs.getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> prefs.getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> prefs.getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

}