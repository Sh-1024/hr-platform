package com.s1024.hr_platform.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.s1024.hr_platform.R
import com.s1024.hr_platform.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onNavigateBack: () -> Unit) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.settings_title)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.switch_theme))
                Switch(
                    checked = isDarkTheme == true,
                    onCheckedChange = { viewModel.toggleTheme(it) }
                )
            }

            Text(stringResource(id = R.string.language_hint), style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { setLanguage("en") }) {
                    Text(stringResource(id = R.string.language_en))
                }
                Button(onClick = { setLanguage("ru") }) {
                    Text(stringResource(id = R.string.language_ru))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.back_button))
            }
        }
    }
}

private fun setLanguage(languageTag: String) {
    val localeList = LocaleListCompat.forLanguageTags(languageTag)
    AppCompatDelegate.setApplicationLocales(localeList)
}