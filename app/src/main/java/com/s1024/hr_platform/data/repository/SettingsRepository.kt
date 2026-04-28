package com.s1024.hr_platform.data.repository

import com.s1024.hr_platform.data.AppThemeMode
import com.s1024.hr_platform.data.ThemePreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val themePreferences: ThemePreferences
) {
    val themeMode: Flow<AppThemeMode> = themePreferences.themeMode

    suspend fun saveThemeMode(themeMode: AppThemeMode) {
        themePreferences.saveThemeMode(themeMode)
    }
}
