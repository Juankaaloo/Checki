package com.example.marcador_horario.ui.features.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.model.ReportDto
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.ReportRepository
import kotlinx.coroutines.launch

class AdminReportsViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // ── Lista de reportes ───────────────────────────────────────────────────
    var allReports      by mutableStateOf<List<ReportDto>>(emptyList())
    var filteredReports by mutableStateOf<List<ReportDto>>(emptyList())

    // ── Filtro activo ───────────────────────────────────────────────────────
    var activeFilter by mutableStateOf("All")

    // ── Contadores ──────────────────────────────────────────────────────────
    var pendingCount  by mutableStateOf(0)
    var approvedCount by mutableStateOf(0)
    var rejectedCount by mutableStateOf(0)

    // ── Estado ──────────────────────────────────────────────────────────────
    var isLoading    by mutableStateOf(true)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        loadReports()
    }

    fun refresh() {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            when (val result = reportRepository.getAll()) {
                is ApiResult.Success -> {
                    allReports = result.data.reports
                    updateCounters()
                    applyFilter()
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }

            isLoading = false
        }
    }

    fun updateFilter(filter: String) {
        activeFilter = filter
        applyFilter()
    }

    fun approveReport(reportId: Int) {
        viewModelScope.launch {
            when (val result = reportRepository.approve(reportId)) {
                is ApiResult.Success -> {
                    // Actualizar la lista local
                    allReports = allReports.map {
                        if (it.id == reportId) it.copy(status = "approved") else it
                    }
                    updateCounters()
                    applyFilter()
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }
        }
    }

    fun rejectReport(reportId: Int) {
        viewModelScope.launch {
            when (val result = reportRepository.reject(reportId)) {
                is ApiResult.Success -> {
                    allReports = allReports.map {
                        if (it.id == reportId) it.copy(status = "rejected") else it
                    }
                    updateCounters()
                    applyFilter()
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }
        }
    }

    private fun updateCounters() {
        pendingCount  = allReports.count { it.status == "pending" }
        approvedCount = allReports.count { it.status == "approved" }
        rejectedCount = allReports.count { it.status == "rejected" }
    }

    private fun applyFilter() {
        filteredReports = when (activeFilter) {
            "Pending"  -> allReports.filter { it.status == "pending" }
            "Approved" -> allReports.filter { it.status == "approved" }
            "Rejected" -> allReports.filter { it.status == "rejected" }
            else       -> allReports
        }
    }

    class Factory(
        private val reportRepository: ReportRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            AdminReportsViewModel(reportRepository) as T
    }
}