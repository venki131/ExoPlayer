package com.example.exovideoplayer

import android.app.Activity

object ThemeManager {
    fun applyLightTheme(activity: Activity) {
        activity.setTheme(R.style.AppThemeLight)
    }

    fun applyDarkTheme(activity: Activity) {
        activity.setTheme(R.style.AppThemeDark)
    }

    var isUserChangeTheme = false
}
