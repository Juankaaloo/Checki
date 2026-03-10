package com.example.marcador_horario.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.marcador_horario.ui.features.login.LoginScreen
import com.example.marcador_horario.ui.features.login.LoginViewModel
import com.example.marcador_horario.ui.features.home.HomeScreen
import com.example.marcador_horario.ui.features.home.HomeViewModel
import com.example.marcador_horario.ui.features.record.RecordScreen
import com.example.marcador_horario.ui.features.record.RecordViewModel
import com.example.marcador_horario.ui.features.settings.SettingsScreen
import com.example.marcador_horario.ui.features.settings.SettingsViewModel
import com.example.marcador_horario.ui.features.admin.AdminScreen
import com.example.marcador_horario.ui.features.admin.AdminViewModel
import com.example.marcador_horario.ui.features.admin.AdminEmployeesScreen
import com.example.marcador_horario.ui.features.admin.AdminEmployeesViewModel
import com.example.marcador_horario.ui.features.admin.AdminReportsScreen
import com.example.marcador_horario.ui.features.admin.AdminReportsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    recordViewModel: RecordViewModel,
    settingsViewModel: SettingsViewModel,
    adminViewModel: AdminViewModel,
    adminEmployeesViewModel: AdminEmployeesViewModel,
    adminReportsViewModel: AdminReportsViewModel
) {
    var activeUsername by remember { mutableStateOf("Employee") }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { username, isAdmin ->
                    activeUsername = username
                    val destino = if (isAdmin) "admin_home" else "home"
                    navController.navigate(destino) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                username      = activeUsername,
                isDarkMode    = isDarkMode,
                viewModel     = homeViewModel
            )
        }

        composable("record") {
            RecordScreen(
                navController = navController,
                isDarkMode    = isDarkMode,
                viewModel     = recordViewModel
            )
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                username      = activeUsername,
                isDarkMode    = isDarkMode,
                onThemeChange = onThemeChange,
                viewModel     = settingsViewModel
            )
        }

        composable("admin_home") {
            AdminScreen(
                navController = navController,
                username      = activeUsername,
                isDarkMode    = isDarkMode,
                viewModel     = adminViewModel
            )
        }

        composable("admin_employees") {
            AdminEmployeesScreen(
                navController = navController,
                isDarkMode    = isDarkMode,
                viewModel     = adminEmployeesViewModel
            )
        }

        composable("admin_reports") {
            AdminReportsScreen(
                navController = navController,
                isDarkMode    = isDarkMode,
                viewModel     = adminReportsViewModel
            )
        }

        composable("admin_settings") {
            SettingsScreen(
                navController = navController,
                username      = activeUsername,
                isDarkMode    = isDarkMode,
                isAdmin       = true,
                onThemeChange = onThemeChange,
                viewModel     = settingsViewModel
            )
        }
    }
}