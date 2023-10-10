package com.example.exovideoplayer

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object ThemeManager {
    fun applyLightTheme(activity: Activity) {
        activity.setTheme(R.style.AppThemeLight)
    }

    fun applyDarkTheme(activity: Activity) {
        activity.setTheme(R.style.AppThemeDark)
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        // Retrieve the user's dark mode preference from your settings or preferences
        // Return true if dark mode is enabled, false otherwise
        return context.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE).getBoolean(
            "dark_mode",
            false
        )
    }

    fun saveThemePreference(context: Context, isDarkMode: Boolean) {
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("dark_mode", isDarkMode)
        editor.apply()
    }
}
