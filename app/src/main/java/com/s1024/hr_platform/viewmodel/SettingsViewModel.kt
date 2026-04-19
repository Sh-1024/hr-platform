package com.s1024.hr_platform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1024.hr_platform.data.AppThemeMode
import com.s1024.hr_platform.data.repository.SettingsRepository
import com.s1024.hr_platform.ui.state.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = settingsRepository.themeMode
        .map(::SettingsUiState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun updateThemeMode(themeMode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepository.saveThemeMode(themeMode)
        }
    }
}
