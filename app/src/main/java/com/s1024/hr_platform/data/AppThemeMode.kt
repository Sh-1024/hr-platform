package com.s1024.hr_platform.data

import androidx.appcompat.app.AppCompatDelegate

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun resolveDarkTheme(isSystemDarkTheme: Boolean): Boolean {
        return when (this) {
            SYSTEM -> isSystemDarkTheme
            LIGHT -> false
            DARK -> true
        }
    }

    fun toNightMode(): Int {
        return when (this) {
            SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            DARK -> AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    companion object {
        fun fromValue(value: String?): AppThemeMode {
            return enumValues<AppThemeMode>().firstOrNull { it.name == value } ?: SYSTEM
        }
    }
}
