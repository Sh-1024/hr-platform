package com.s1024.hr_platform.ui.state

import com.s1024.hr_platform.data.AppThemeMode

data class SettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM
)
