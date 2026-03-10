package com.example.marcador_horario.data.repository

import com.example.marcador_horario.data.model.*
import com.example.marcador_horario.data.network.*

// ═══════════════════════════════════════════════════════════════════════════
// AUTH REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): ApiResult<UserDto> {
        if (email.isBlank() || password.isBlank())
            return ApiResult.Error("Email y contraseña son obligatorios")

        return when (val result = safeApiCall {
            api.login(LoginRequest(email.trim().lowercase(), password))
        }) {
            is ApiResult.Success -> {
                sessionManager.saveSession(result.data.token, result.data.user)
                ApiResult.Success(result.data.user)
            }
            is ApiResult.Error   -> result
            is ApiResult.Loading -> result
        }
    }

    suspend fun register(
        name: String, email: String, password: String, jobTitle: String? = null
    ): ApiResult<UserDto> {
        return when (val result = safeApiCall {
            api.register(RegisterRequest(name, email, password, jobTitle))
        }) {
            is ApiResult.Success -> {
                sessionManager.saveSession(result.data.token, result.data.user)
                ApiResult.Success(result.data.user)
            }
            is ApiResult.Error   -> result
            is ApiResult.Loading -> result
        }
    }

    suspend fun getMe(): ApiResult<UserDto> = safeApiCall { api.me() }
}

// ═══════════════════════════════════════════════════════════════════════════
// ATTENDANCE REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class AttendanceRepository(
    private val api: AttendanceApiService
) {
    suspend fun checkIn(location: String = "office"): ApiResult<AttendanceDto> =
        safeApiCall { api.checkIn(CheckInRequest(location)) }

    suspend fun checkOut(): ApiResult<AttendanceDto> =
        safeApiCall { api.checkOut() }

    suspend fun startBreak(): ApiResult<BreakDto> =
        safeApiCall { api.startBreak() }

    suspend fun endBreak(): ApiResult<BreakDto> =
        safeApiCall { api.endBreak() }

    suspend fun getToday(): ApiResult<AttendanceDto> =
        safeApiCall { api.getToday() }

    suspend fun getHistory(month: String? = null): ApiResult<AttendanceHistoryDto> =
        safeApiCall { api.getHistory(month = month) }

    suspend fun weeklyStats(): ApiResult<WeeklyStatsDto> =
        safeApiCall { api.weeklyStats() }
}

// ═══════════════════════════════════════════════════════════════════════════
// REPORTS REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class ReportRepository(
    private val api: ReportsApiService
) {
    suspend fun getAll(status: String? = null, type: String? = null): ApiResult<ReportsListDto> =
        safeApiCall { api.getAll(status, type) }

    suspend fun getStats(): ApiResult<ReportStatsDto> =
        safeApiCall { api.stats() }

    suspend fun create(
        type: String,
        reason: String? = null,
        description: String,
        date: String
    ): ApiResult<ReportDto> {
        if (description.isBlank())
            return ApiResult.Error("La descripción es obligatoria")
        return safeApiCall {
            api.create(CreateReportRequest(type, reason, description, date))
        }
    }

    suspend fun approve(id: Int): ApiResult<ReportDto> =
        safeApiCall { api.updateStatus(id, UpdateReportStatusRequest("approved")) }

    suspend fun reject(id: Int): ApiResult<ReportDto> =
        safeApiCall { api.updateStatus(id, UpdateReportStatusRequest("rejected")) }

    suspend fun resolve(id: Int): ApiResult<ReportDto> =
        safeApiCall { api.updateStatus(id, UpdateReportStatusRequest("resolved")) }

    suspend fun delete(id: Int): ApiResult<MessageResponse> =
        safeApiCall { api.delete(id) }
}

// ═══════════════════════════════════════════════════════════════════════════
// EMPLOYEES REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class EmployeesRepository(
    private val api: EmployeesApiService
) {
    suspend fun getAll(
        department: String? = null,
        status: String? = null,
        search: String? = null
    ): ApiResult<List<UserDto>> =
        safeApiCall { api.getAll(department, status, search) }

    suspend fun getById(id: Int): ApiResult<UserDto> =
        safeApiCall { api.getById(id) }

    suspend fun update(id: Int, data: EmployeeUpdateRequest): ApiResult<UserDto> =
        safeApiCall { api.update(id, data) }

    suspend fun delete(id: Int): ApiResult<MessageResponse> =
        safeApiCall { api.delete(id) }

    suspend fun departmentStats(): ApiResult<List<DepartmentStatsDto>> =
        safeApiCall { api.departmentStats() }
}

// ═══════════════════════════════════════════════════════════════════════════
// SETTINGS REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class SettingsRepository(
    private val api: SettingsApiService,
    private val sessionManager: SessionManager
) {
    suspend fun getSettings(): ApiResult<UserSettingsDto> =
        safeApiCall { api.get() }

    suspend fun updateSettings(data: UpdateSettingsRequest): ApiResult<UserSettingsDto> =
        safeApiCall { api.update(data) }

    suspend fun updateProfile(data: UpdateProfileRequest): ApiResult<UserDto> =
        when (val result = safeApiCall { api.updateProfile(data) }) {
            is ApiResult.Success -> {
                sessionManager.updateProfile(result.data)
                result
            }
            else -> result
        }

    suspend fun updatePassword(
        currentPassword: String, newPassword: String
    ): ApiResult<MessageResponse> {
        if (newPassword.length < 6)
            return ApiResult.Error("La contraseña debe tener al menos 6 caracteres")
        return safeApiCall {
            api.updatePassword(UpdatePasswordRequest(currentPassword, newPassword))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// NOTIFICATIONS REPOSITORY
// ═══════════════════════════════════════════════════════════════════════════
class NotificationsRepository(
    private val api: NotificationsApiService
) {
    suspend fun getAll(): ApiResult<List<NotificationDto>> =
        safeApiCall { api.getAll() }

    suspend fun markAllRead(): ApiResult<MessageResponse> =
        safeApiCall { api.markAllRead() }

    suspend fun markRead(id: Int): ApiResult<MessageResponse> =
        safeApiCall { api.markRead(id) }
}