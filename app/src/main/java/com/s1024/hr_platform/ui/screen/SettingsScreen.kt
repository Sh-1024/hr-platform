package com.s1024.hr_platform.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.s1024.hr_platform.R
import com.s1024.hr_platform.data.AppThemeMode
import com.s1024.hr_platform.viewmodel.SettingsViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.settings_title)) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = stringResource(id = R.string.theme_section_title), style = MaterialTheme.typography.titleMedium)

            ThemeModeRow(titleRes = R.string.theme_system, selected = uiState.themeMode == AppThemeMode.SYSTEM) { viewModel.updateThemeMode(AppThemeMode.SYSTEM) }
            ThemeModeRow(titleRes = R.string.theme_light, selected = uiState.themeMode == AppThemeMode.LIGHT) { viewModel.updateThemeMode(AppThemeMode.LIGHT) }
            ThemeModeRow(titleRes = R.string.theme_dark, selected = uiState.themeMode == AppThemeMode.DARK) { viewModel.updateThemeMode(AppThemeMode.DARK) }

            Text(stringResource(id = R.string.language_hint), style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { setLanguage("en") }) { Text(stringResource(id = R.string.language_en)) }
                Button(onClick = { setLanguage("ru") }) { Text(stringResource(id = R.string.language_ru)) }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(id = R.string.logout_button))
            }

            Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.back_button))
            }
        }
    }
}

@Composable
private fun ThemeModeRow(
    @StringRes titleRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (selected) 3.dp else 0.dp,
        color = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = titleRes))
            RadioButton(selected = selected, onClick = null)
        }
    }
}

private fun setLanguage(languageTag: String) {
    val localeList = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(localeList)
}
