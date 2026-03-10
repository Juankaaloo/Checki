package com.example.marcador_horario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.marcador_horario.navigation.NavGraph
import com.example.marcador_horario.ui.features.admin.AdminViewModel
import com.example.marcador_horario.ui.features.admin.AdminEmployeesViewModel
import com.example.marcador_horario.ui.features.admin.AdminReportsViewModel
import com.example.marcador_horario.ui.features.home.HomeViewModel
import com.example.marcador_horario.ui.features.login.LoginViewModel
import com.example.marcador_horario.ui.features.record.RecordViewModel
import com.example.marcador_horario.ui.features.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val container get() = (application as MarcadorHorarioApp).container

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(container.authRepository)
    }
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(container.attendanceRepository, container.sessionManager)
    }
    private val recordViewModel: RecordViewModel by viewModels {
        RecordViewModel.Factory(container.attendanceRepository)
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(
            container.settingsRepository,
            container.sessionManager,
            container.localeManager
        )
    }

    // ── Nuevos ViewModels Admin ─────────────────────────────────────────────
    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModel.Factory(
            container.employeesRepository,
            container.reportRepository,
            container.sessionManager
        )
    }
    private val adminEmployeesViewModel: AdminEmployeesViewModel by viewModels {
        AdminEmployeesViewModel.Factory(container.employeesRepository)
    }
    private val adminReportsViewModel: AdminReportsViewModel by viewModels {
        AdminReportsViewModel.Factory(container.reportRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.attachContext(this)

        setContent {
            var isDarkMode by remember { mutableStateOf(true) }
            val navController = rememberNavController()

            NavGraph(
                navController           = navController,
                isDarkMode              = isDarkMode,
                onThemeChange           = { isDarkMode = it },
                loginViewModel          = loginViewModel,
                homeViewModel           = homeViewModel,
                recordViewModel         = recordViewModel,
                settingsViewModel       = settingsViewModel,
                adminViewModel          = adminViewModel,
                adminEmployeesViewModel = adminEmployeesViewModel,
                adminReportsViewModel   = adminReportsViewModel
            )
        }
    }
}