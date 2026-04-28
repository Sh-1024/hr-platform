package com.s1024.hr_platform.ui.navigation

import VacancyViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.s1024.hr_platform.auth.SessionManager
import com.s1024.hr_platform.ui.screen.AuthScreen
import com.s1024.hr_platform.ui.screen.DetailScreen
import com.s1024.hr_platform.ui.screen.MainScreen
import com.s1024.hr_platform.ui.screen.SettingsScreen
import com.s1024.hr_platform.viewmodel.AuthViewModel
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import com.s1024.hr_platform.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Main : Screen("main")
    object Settings : Screen("settings")
    object Details : Screen("details/{vacancyId}") {
        fun createRoute(vacancyId: Int) = "details/$vacancyId"
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
    val coroutineScope = rememberCoroutineScope()

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