package com.s1024.hr_platform

import VacancyViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.s1024.hr_platform.data.AppDatabase
import com.s1024.hr_platform.data.ThemePreferences
import com.s1024.hr_platform.data.VacancyApiService
import com.s1024.hr_platform.data.network.HoroscopeApi
import com.s1024.hr_platform.data.network.NetworkMonitor
import com.s1024.hr_platform.data.network.NotificationHelper
import com.s1024.hr_platform.data.repository.HoroscopeRepository
import com.s1024.hr_platform.data.repository.SettingsRepository
import com.s1024.hr_platform.data.repository.VacancyRepository
import com.s1024.hr_platform.ui.navigation.AppNavigation
import com.s1024.hr_platform.ui.theme.HrplatformTheme
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import com.s1024.hr_platform.viewmodel.SettingsViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val themePreferences = ThemePreferences(this)
        val networkMonitor = NetworkMonitor(this)

        val notificationHelper = NotificationHelper(this)

        val horoscopeRetrofit = Retrofit.Builder()
            .baseUrl("https://api.apiverve.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val horoscopeApi = horoscopeRetrofit.create(HoroscopeApi::class.java)

        val springBootRetrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val vacancyApi = springBootRetrofit.create(VacancyApiService::class.java)

        val horoscopeRepo = HoroscopeRepository(horoscopeApi, database.horoscopeDao())
        val settingsRepository = SettingsRepository(themePreferences)
        val vacancyRepository = VacancyRepository(database.vacancyDao(), vacancyApi, notificationHelper)


        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                        SettingsViewModel(settingsRepository) as T
                    modelClass.isAssignableFrom(VacancyViewModel::class.java) ->
                        VacancyViewModel(vacancyRepository) as T
                    modelClass.isAssignableFrom(HoroscopeViewModel::class.java) ->
                        HoroscopeViewModel(horoscopeRepo, networkMonitor) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        setContent {
            val horoscopeViewModel: HoroscopeViewModel = viewModel(factory = factory)
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val vacancyViewModel: VacancyViewModel = viewModel(factory = factory)

            val navController = rememberNavController()

            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme = settingsUiState.themeMode.resolveDarkTheme(isSystemInDarkTheme())

            SideEffect {
                AppCompatDelegate.setDefaultNightMode(settingsUiState.themeMode.toNightMode())
            }

            HrplatformTheme(
                darkTheme = isDarkTheme,
                dynamicColor = false
            ) {
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