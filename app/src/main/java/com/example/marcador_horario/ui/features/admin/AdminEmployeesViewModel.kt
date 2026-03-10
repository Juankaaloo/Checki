package com.example.marcador_horario.ui.features.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.model.UserDto
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.EmployeesRepository
import kotlinx.coroutines.launch

class AdminEmployeesViewModel(
    private val employeesRepository: EmployeesRepository
) : ViewModel() {

    // ── Lista de empleados ──────────────────────────────────────────────────
    var allEmployees by mutableStateOf<List<UserDto>>(emptyList())
    var filteredEmployees by mutableStateOf<List<UserDto>>(emptyList())

    // ── Filtros ─────────────────────────────────────────────────────────────
    var searchQuery      by mutableStateOf("")
    var statusFilter     by mutableStateOf("All")
    var departmentFilter by mutableStateOf("All")

    // ── Contadores ──────────────────────────────────────────────────────────
    var workingCount by mutableStateOf(0)
    var breakCount   by mutableStateOf(0)
    var absentCount  by mutableStateOf(0)

    // ── Departamentos disponibles ───────────────────────────────────────────
    var departments by mutableStateOf(listOf("All"))

    // ── Estado ──────────────────────────────────────────────────────────────
    var isLoading    by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadEmployees()
    }

    fun refresh() {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            when (val result = employeesRepository.getAll()) {
                is ApiResult.Success -> {
                    allEmployees = result.data

                    // Extraer departamentos únicos
                    departments = listOf("All") +
                            result.data.mapNotNull { it.department }.distinct().sorted()

                    // Calcular contadores (is_active como working/absent)
                    workingCount = result.data.count { it.isActive }
                    absentCount  = result.data.count { !it.isActive }
                    breakCount   = 0 // La API no devuelve estado de break en empleados

                    applyFilters()
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }

            isLoading = false
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        applyFilters()
    }

    fun updateStatusFilter(filter: String) {
        statusFilter = filter
        applyFilters()
    }

    fun updateDepartmentFilter(filter: String) {
        departmentFilter = filter
        applyFilters()
    }

    fun clearFilters() {
        searchQuery = ""
        statusFilter = "All"
        departmentFilter = "All"
        applyFilters()
    }

    private fun applyFilters() {
        filteredEmployees = allEmployees.filter { emp ->
            val matchSearch = searchQuery.isBlank() ||
                    emp.name.contains(searchQuery, ignoreCase = true) ||
                    (emp.department ?: "").contains(searchQuery, ignoreCase = true) ||
                    (emp.jobTitle ?: "").contains(searchQuery, ignoreCase = true)

            val matchStatus = when (statusFilter) {
                "Working" -> emp.isActive
                "Absent"  -> !emp.isActive
                else      -> true
            }

            val matchDept = departmentFilter == "All" ||
                    emp.department == departmentFilter

            matchSearch && matchStatus && matchDept
        }
    }

    class Factory(
        private val employeesRepository: EmployeesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            AdminEmployeesViewModel(employeesRepository) as T
    }
}