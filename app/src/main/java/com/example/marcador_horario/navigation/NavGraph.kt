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

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // --- MEMORIA GLOBAL ---
    val systemTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemTheme) }

    // 1. Creamos una memoria para guardar el nombre de quien entra
    var activeUsername by remember { mutableStateOf("Employee") }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = { username ->
                // 2. Cuando el login es correcto, guardamos el nombre
                activeUsername = username

                // 3. Vamos al home (ruta simplificada)
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home") {
            // Le pasamos el nombre real
            HomeScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode)
        }

        composable("record") {
            RecordScreen(navController = navController, isDarkMode = isDarkMode)
        }

        composable("settings") {
            // Le pasamos el nombre real a los ajustes también
            SettingsScreen(
                navController = navController,
                username = activeUsername,
                isDarkMode = isDarkMode,
                onThemeChange = { isDarkMode = it }
            )
        }
    }
}