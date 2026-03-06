package com.example.marcador_horario.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.marcador_horario.ui.features.login.LoginScreen
import com.example.marcador_horario.ui.features.home.HomeScreen
import com.example.marcador_horario.ui.features.home.HomeViewModel
import com.example.marcador_horario.ui.features.record.RecordScreen
import com.example.marcador_horario.ui.features.settings.SettingsScreen
import com.example.marcador_horario.ui.features.admin.AdminScreen
// Importamos las nuevas pantallas del admin (Las crearemos en el Paso 3)
import com.example.marcador_horario.ui.features.admin.AdminEmployeesScreen
import com.example.marcador_horario.ui.features.admin.AdminReportsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val systemTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemTheme) }
    var activeUsername by remember { mutableStateOf("Employee") }

    val globalHomeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { username, isAdmin ->
                    activeUsername = username
                    val rutaDestino = if (isAdmin) "admin_home" else "home"
                    navController.navigate(rutaDestino) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode, viewModel = globalHomeViewModel)
        }

        composable("record") {
            RecordScreen(navController = navController, isDarkMode = isDarkMode)
        }

        composable("settings") {
            SettingsScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode, onThemeChange = { isDarkMode = it })
        }

        // --- RUTAS DEL ADMINISTRADOR ---
        composable("admin_home") {
            AdminScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode, viewModel = globalHomeViewModel)
        }

        composable("admin_employees") {
            AdminEmployeesScreen(navController = navController, isDarkMode = isDarkMode)
        }

        composable("admin_reports") {
            AdminReportsScreen(navController = navController, isDarkMode = isDarkMode)
        }

        // Puedes redirigir los ajustes del admin a los ajustes normales por ahora,
        // o crear una pantalla nueva más adelante.
        composable("admin_settings") {
            SettingsScreen(
                navController = navController,
                username = activeUsername,
                isDarkMode = isDarkMode,
                isAdmin = true, // <--- ¡AÑADE ESTO AQUÍ!
                onThemeChange = { isDarkMode = it }
            )
        }
    }
}