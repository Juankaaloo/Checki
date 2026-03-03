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
import com.example.marcador_horario.ui.features.admin.AdminScreen

/**
 * Componente principal de navegación (NavGraph).
 * Actúa como el "Single Source of Truth" (Única fuente de verdad) para el estado global
 * de la aplicación, como el tema (Dark/Light) y el usuario activo, propagando estos datos
 * hacia las pantallas hijas mediante el patrón State Hoisting.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // --- ESTADO GLOBAL ---
    // Detectamos el tema del sistema al inicio para establecer el valor por defecto.
    val systemTheme = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemTheme) }

    // Almacena el nombre del usuario logueado para mostrarlo en las distintas pantallas.
    var activeUsername by remember { mutableStateOf("Employee") }

    NavHost(navController = navController, startDestination = "login") {

        // --- RUTA: LOGIN ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = { username, isAdmin ->
                    activeUsername = username // Guardamos el usuario en el estado global

                    // Lógica RBAC (Role-Based Access Control)
                    // Redirigimos al dashboard correspondiente según el rol del usuario.
                    val rutaDestino = if (isAdmin) "admin_home" else "home"

                    navController.navigate(rutaDestino) {
                        // Limpiamos el backstack para que el usuario no pueda volver al login con el botón "Atrás"
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- RUTA: PORTAL DEL EMPLEADO ---
        composable("home") {
            HomeScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode)
        }

        // --- RUTA: PORTAL DEL ADMINISTRADOR ---
        composable("admin_home") {
            AdminScreen(navController = navController, username = activeUsername, isDarkMode = isDarkMode)
        }

        // --- RUTA: HISTORIAL DE FICHAJES ---
        composable("record") {
            RecordScreen(navController = navController, isDarkMode = isDarkMode)
        }

        // --- RUTA: CONFIGURACIÓN ---
        composable("settings") {
            SettingsScreen(
                navController = navController,
                username = activeUsername,
                isDarkMode = isDarkMode,
                // Pasamos la función lambda para que SettingsScreen pueda modificar el estado global
                onThemeChange = { isDarkMode = it }
            )
        }
    }
}