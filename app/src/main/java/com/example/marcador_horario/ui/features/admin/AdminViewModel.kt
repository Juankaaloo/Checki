package com.example.marcador_horario.ui.features.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.model.DepartmentStatsDto
import com.example.marcador_horario.data.model.ReportStatsDto
import com.example.marcador_horario.data.model.UserDto
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.EmployeesRepository
import com.example.marcador_horario.data.repository.ReportRepository
import com.example.marcador_horario.data.repository.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ActivityEntry(
    val name: String,
    val action: String,
    val time: String,
    val isEntry: Boolean,
    val location: String = "Office"
)

class AdminViewModel(
    private val employeesRepository: EmployeesRepository,
    private val reportRepository: ReportRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // ── Datos de sesión ─────────────────────────────────────────────────────
    var userName     by mutableStateOf("")
    var currentTime  by mutableStateOf("")
    var currentAmPm  by mutableStateOf("")
    var currentDate  by mutableStateOf("")

    // ── KPIs ────────────────────────────────────────────────────────────────
    var totalEmployees   by mutableStateOf(0)
    var activeEmployees  by mutableStateOf(0)
    var absentEmployees  by mutableStateOf(0)
    var onBreakEmployees by mutableStateOf(0)
    var pendingReports   by mutableStateOf(0)
    var attendanceRate   by mutableStateOf(0f)

    // ── Listas ──────────────────────────────────────────────────────────────
    var employees    by mutableStateOf<List<UserDto>>(emptyList())
    var activityList by mutableStateOf<List<ActivityEntry>>(emptyList())
    var departmentStats by mutableStateOf<List<DepartmentStatsDto>>(emptyList())
    var reportStats  by mutableStateOf<ReportStatsDto?>(null)

    // ── Estado de carga ─────────────────────────────────────────────────────
    var isLoading    by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    private var clockJob: kotlinx.coroutines.Job? = null

    init {
        startClock()
        loadSession()
        loadData()
    }

    private fun loadSession() {
        viewModelScope.launch {
            userName = sessionManager.username.first()
        }
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            // Cargar empleados
            when (val result = employeesRepository.getAll()) {
                is ApiResult.Success -> {
                    employees = result.data
                    totalEmployees = result.data.size
                    activeEmployees = result.data.count { it.isActive }
                    absentEmployees = result.data.count { !it.isActive }
                    attendanceRate = if (totalEmployees > 0)
                        activeEmployees.toFloat() / totalEmployees.toFloat()
                    else 0f

                    // Generar actividad reciente a partir de empleados reales
                    generateActivityFromEmployees(result.data)
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }

            // Cargar stats de departamentos
            when (val result = employeesRepository.departmentStats()) {
                is ApiResult.Success -> {
                    departmentStats = result.data
                }
                is ApiResult.Error -> { /* silencioso */ }
                else -> {}
            }

            // Cargar stats de reportes
            when (val result = reportRepository.getStats()) {
                is ApiResult.Success -> {
                    reportStats = result.data
                    pendingReports = result.data.pending
                }
                is ApiResult.Error -> { /* silencioso */ }
                else -> {}
            }

            isLoading = false
        }
    }

    private fun generateActivityFromEmployees(employees: List<UserDto>) {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val now = Date()
        val actions = listOf("Clock In", "Clock Out", "Break")

        activityList = employees
            .filter { it.isActive }
            .shuffled()
            .take(6)
            .mapIndexed { index, emp ->
                val action = actions[index % actions.size]
                ActivityEntry(
                    name     = emp.name,
                    action   = action,
                    time     = fmt.format(Date(now.time - (index + 1) * 600_000L)),
                    isEntry  = action == "Clock In",
                    location = emp.location.replaceFirstChar { it.uppercase() }
                )
            }
    }

    private fun startClock() {
        clockJob = viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                currentTime = SimpleDateFormat("hh:mm", Locale.getDefault()).format(cal.time)
                currentAmPm = SimpleDateFormat("a", Locale.getDefault()).format(cal.time)
                currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(cal.time)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
    }

    class Factory(
        private val employeesRepository: EmployeesRepository,
        private val reportRepository: ReportRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            AdminViewModel(employeesRepository, reportRepository, sessionManager) as T
    }
}