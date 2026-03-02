package com.example.marcador_horario.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.marcador_horario.ui.features.login.LoginScreen
import com.example.marcador_horario.ui.features.home.HomeScreen
import com.example.marcador_horario.ui.features.settings.SettingsScreen
import com.example.marcador_horario.ui.features.record.RecordScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Pantalla 1: Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { nombreExtraido ->
                    // IMPORTANTE: La ruta debe coincidir con la de abajo "home/nombre"
                    navController.navigate("home/$nombreExtraido") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 2: Home (Acepta un nombre de usuario)
        composable(
            route = "home/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val uName = backStackEntry.arguments?.getString("username") ?: "Employee"
            HomeScreen(navController = navController, username = uName)
        }

        // Pantallas 3 y 4: Records y Settings
        composable("record") { RecordScreen(navController = navController) }

        composable("settings") { SettingsScreen(navController = navController) }
    }
}