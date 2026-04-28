package com.s1024.hr_platform

import VacancyViewModel
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.s1024.hr_platform.auth.SessionManager
import com.s1024.hr_platform.data.AppDatabase
import com.s1024.hr_platform.data.ThemePreferences
import com.s1024.hr_platform.data.VacancyApiService
import com.s1024.hr_platform.data.network.HoroscopeApi
import com.s1024.hr_platform.data.network.NetworkMonitor
import com.s1024.hr_platform.data.network.NotificationHelper
import com.s1024.hr_platform.data.repository.HoroscopeRepository
import com.s1024.hr_platform.data.repository.SettingsRepository
import com.s1024.hr_platform.data.repository.VacancyRepository
import com.s1024.hr_platform.ui.navigation.Screen
import com.s1024.hr_platform.ui.theme.HrplatformTheme
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import com.s1024.hr_platform.viewmodel.SettingsViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.s1024.hr_platform.auth.AuthApiService
import com.s1024.hr_platform.ui.screen.AuthScreen
import com.s1024.hr_platform.ui.screen.DetailScreen
import com.s1024.hr_platform.ui.screen.MainScreen
import com.s1024.hr_platform.ui.screen.SettingsScreen
import com.s1024.hr_platform.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        
        val database = AppDatabase.getDatabase(this)
        val themePreferences = ThemePreferences(this)
        val networkMonitor = NetworkMonitor(this)
        val notificationHelper = NotificationHelper(this)
        val sessionManager = SessionManager(this) 

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
        val authApi = springBootRetrofit.create(AuthApiService::class.java)

        val horoscopeRepo = HoroscopeRepository(horoscopeApi, database.horoscopeDao())
        val settingsRepository = SettingsRepository(themePreferences)
        val vacancyRepository = VacancyRepository(database.vacancyDao(), vacancyApi, notificationHelper)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(settingsRepository) as T
                    modelClass.isAssignableFrom(VacancyViewModel::class.java) -> VacancyViewModel(vacancyRepository) as T
                    modelClass.isAssignableFrom(HoroscopeViewModel::class.java) -> HoroscopeViewModel(horoscopeRepo, networkMonitor) as T
                    modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(authApi, sessionManager) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        setContent {
            val horoscopeViewModel: HoroscopeViewModel = viewModel(factory = factory)
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val vacancyViewModel: VacancyViewModel = viewModel(factory = factory)
            val authViewModel: AuthViewModel = viewModel(factory = factory)

            val navController = rememberNavController()

            val currentUser by sessionManager.currentUser.collectAsStateWithLifecycle(initialValue = "LOADING")
            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme = settingsUiState.themeMode.resolveDarkTheme(isSystemInDarkTheme())

            SideEffect {
                AppCompatDelegate.setDefaultNightMode(settingsUiState.themeMode.toNightMode())
            }

            HrplatformTheme(
                darkTheme = isDarkTheme,
                dynamicColor = false
            ) {
                if (currentUser == "LOADING") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppNavigation(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        vacancyViewModel = vacancyViewModel,
                        horoscopeViewModel = horoscopeViewModel,
                        authViewModel = authViewModel,
                        sessionManager = sessionManager,
                        currentUser = currentUser
                    )
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    vacancyViewModel: VacancyViewModel,
    horoscopeViewModel: HoroscopeViewModel,
    authViewModel: AuthViewModel, 
    sessionManager: SessionManager,
    currentUser: String?
) {
    val startDestination = if (currentUser.isNullOrBlank()) Screen.Auth.route else Screen.Main.route

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Auth.route) {
            AuthScreen(
                authViewModel = authViewModel, 
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                viewModel = vacancyViewModel,
                horoscopeViewModel = horoscopeViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDetails = { id ->
                    if (id == 0 && !currentUser.isNullOrBlank()) {
                        vacancyViewModel.updateAuthor(currentUser)
                    }
                    navController.navigate(Screen.Details.createRoute(id))
                }
            )
        }

        composable(Screen.Settings.route) {
            val coroutineScope = rememberCoroutineScope()

            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    coroutineScope.launch {
                        sessionManager.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("vacancyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val vacancyId = backStackEntry.arguments?.getInt("vacancyId") ?: 0
            DetailScreen(
                vacancyId = vacancyId,
                viewModel = vacancyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}