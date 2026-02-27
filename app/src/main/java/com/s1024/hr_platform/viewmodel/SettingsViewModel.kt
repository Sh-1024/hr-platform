package com.s1024.hr_platform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1024.hr_platform.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val themePreferences: ThemePreferences) : ViewModel() {

    val isDarkTheme: StateFlow<Boolean?> = themePreferences.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.saveTheme(isDark)
        }
    }
}