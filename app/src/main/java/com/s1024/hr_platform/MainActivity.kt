package com.s1024.hr_platform

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
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
import com.s1024.hr_platform.data.network.HoroscopeApi
import com.s1024.hr_platform.data.network.NetworkMonitor
import com.s1024.hr_platform.data.repository.HoroscopeRepository
import com.s1024.hr_platform.ui.navigation.AppNavigation
import com.s1024.hr_platform.viewmodel.SettingsViewModel
import com.s1024.hr_platform.viewmodel.VacancyViewModel
import com.s1024.hr_platform.ui.theme.HrplatformTheme
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val themePreferences = ThemePreferences(this)
        val networkMonitor = NetworkMonitor(this)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.apiverve.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val horoscopeApi = retrofit.create(HoroscopeApi::class.java)

        val horoscopeRepo = HoroscopeRepository(horoscopeApi, database.horoscopeDao())

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(themePreferences) as T
                    modelClass.isAssignableFrom(VacancyViewModel::class.java) -> VacancyViewModel(database.vacancyDao()) as T
                    modelClass.isAssignableFrom(HoroscopeViewModel::class.java) -> HoroscopeViewModel(horoscopeRepo, networkMonitor) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        setContent {
            val horoscopeViewModel: HoroscopeViewModel = viewModel(factory = factory)
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val vacancyViewModel: VacancyViewModel = viewModel(factory = factory)

            val navController = rememberNavController()

            val savedTheme by settingsViewModel.isDarkTheme.collectAsState()
            val isDarkTheme = savedTheme ?: isSystemInDarkTheme()

            HrplatformTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    vacancyViewModel = vacancyViewModel,
                    horoscopeViewModel = horoscopeViewModel
                )
            }
        }
    }
}
