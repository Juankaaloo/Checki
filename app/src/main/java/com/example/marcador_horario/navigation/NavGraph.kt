package com.example.marcador_horario.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.marcador_horario.ui.features.login.LoginScreen
import com.example.marcador_horario.ui.features.home.HomeScreen
import com.example.marcador_horario.ui.features.record.RecordScreen
import com.example.marcador_horario.ui.features.settings.SettingsScreen
import com.example.marcador_horario.ui.features.admin.AdminScreen // <-- IMPORTAMOS LA NUEVA PANTALLA

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val systemTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemTheme) }
    var activeUsername by remember { mutableStateOf("Employee") }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = { username, isAdmin ->
                activeUsername = username

                // --- MAGIA DEL RUTEO ---
                // Si es admin, va a la ruta "admin_home", si no, a "home"
                val rutaDestino = if (isAdmin) "admin_home" else "home"

                navController.navigate(rutaDestino) {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // Ruta del empleado normal
        composable("home") {
            HomeScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode)
        }

        // Ruta exclusiva del Administrador
        composable("admin_home") {
            AdminScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode)
        }

        composable("record") {
            RecordScreen(navController = navController, isDarkMode = isDarkMode)
        }

        composable("settings") {
            SettingsScreen(
                navController = navController,
                username = activeUsername,
                isDarkMode = isDarkMode,
                onThemeChange = { isDarkMode = it }
            )
        }
    }
}