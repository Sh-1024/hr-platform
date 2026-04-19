package com.s1024.hr_platform.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {
    private val legacyThemeKey = booleanPreferencesKey("is_dark_theme")
    private val themeModeKey = stringPreferencesKey("theme_mode")

    val themeMode: Flow<AppThemeMode> = context.dataStore.data.map { preferences ->
        preferences[themeModeKey]?.let(AppThemeMode::fromValue)
            ?: preferences[legacyThemeKey]?.let { isDarkTheme ->
                if (isDarkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT
            }
            ?: AppThemeMode.SYSTEM
    }

    suspend fun saveThemeMode(themeMode: AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeModeKey] = themeMode.name
            preferences.remove(legacyThemeKey)
        }
    }
}
