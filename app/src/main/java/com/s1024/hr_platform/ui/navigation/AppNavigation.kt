package com.s1024.hr_platform.ui.navigation

import VacancyViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.s1024.hr_platform.ui.screen.DetailScreen
import com.s1024.hr_platform.ui.screen.MainScreen
import com.s1024.hr_platform.ui.screen.SettingsScreen
import com.s1024.hr_platform.viewmodel.HoroscopeViewModel
import com.s1024.hr_platform.viewmodel.SettingsViewModel

sealed class Screen(val route: String) {
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
    horoscopeViewModel: HoroscopeViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {

        composable(Screen.Main.route) {
            MainScreen(
                viewModel = vacancyViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDetails = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                horoscopeViewModel = horoscopeViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
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