package com.s1024.hr_platform

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.s1024.hr_platform.data.AppDatabase
import com.s1024.hr_platform.data.ThemePreferences
import com.s1024.hr_platform.ui.navigation.AppNavigation
import com.s1024.hr_platform.viewmodel.SettingsViewModel
import com.s1024.hr_platform.viewmodel.VacancyViewModel
import com.s1024.hr_platform.ui.theme.HrplatformTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val themePreferences = ThemePreferences(this)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    return SettingsViewModel(themePreferences) as T
                }
                if (modelClass.isAssignableFrom(VacancyViewModel::class.java)) {
                    return VacancyViewModel(database.vacancyDao()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val vacancyViewModel: VacancyViewModel = viewModel(factory = factory)

            val navController = rememberNavController()

            val savedTheme by settingsViewModel.isDarkTheme.collectAsState()
            val isDarkTheme = savedTheme ?: isSystemInDarkTheme()

            HrplatformTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    vacancyViewModel = vacancyViewModel
                )
            }
        }
    }
}
