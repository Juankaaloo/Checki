package com.example.marcador_horario.data.network

import com.example.marcador_horario.data.model.*
import retrofit2.Response
import retrofit2.http.*

// ═══════════════════════════════════════════════════════════════════════════
// AUTH SERVICE  →  /api/auth
// ═══════════════════════════════════════════════════════════════════════════
interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun me(): Response<UserDto>
}

// ═══════════════════════════════════════════════════════════════════════════
// EMPLOYEES SERVICE  →  /api/employees
// ═══════════════════════════════════════════════════════════════════════════
interface EmployeesApiService {

    @GET("employees")
    suspend fun getAll(
        @Query("department") department: String? = null,
        @Query("status")     status: String? = null,
        @Query("search")     search: String? = null
    ): Response<List<UserDto>>

    @GET("employees/{id}")
    suspend fun getById(@Path("id") id: Int): Response<UserDto>

    @POST("employees")
    suspend fun create(@Body data: RegisterRequest): Response<UserDto>

    @PUT("employees/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body data: EmployeeUpdateRequest
    ): Response<UserDto>

    @DELETE("employees/{id}")
    suspend fun delete(@Path("id") id: Int): Response<MessageResponse>

    @GET("employees/stats/departments")
    suspend fun departmentStats(): Response<List<DepartmentStatsDto>>
}

// ═══════════════════════════════════════════════════════════════════════════
// ATTENDANCE SERVICE  →  /api/attendance
// ═══════════════════════════════════════════════════════════════════════════
interface AttendanceApiService {

    @POST("attendance/check-in")
    suspend fun checkIn(@Body request: CheckInRequest): Response<AttendanceDto>

    @POST("attendance/check-out")
    suspend fun checkOut(): Response<AttendanceDto>

    @POST("attendance/break/start")
    suspend fun startBreak(): Response<BreakDto>

    @POST("attendance/break/end")
    suspend fun endBreak(): Response<BreakDto>

    @GET("attendance/today")
    suspend fun getToday(): Response<AttendanceDto>

    @GET("attendance/history")
    suspend fun getHistory(
        @Query("month")  month: String? = null,   // "2025-06"
        @Query("limit")  limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<AttendanceHistoryDto>

    @GET("attendance/weekly-stats")
    suspend fun weeklyStats(): Response<WeeklyStatsDto>
}

// ═══════════════════════════════════════════════════════════════════════════
// REPORTS SERVICE  →  /api/reports
// ═══════════════════════════════════════════════════════════════════════════
interface ReportsApiService {

    @GET("reports")
    suspend fun getAll(
        @Query("status") status: String? = null,
        @Query("type")   type: String? = null
    ): Response<ReportsListDto>

    @GET("reports/stats")
    suspend fun stats(): Response<ReportStatsDto>

    @POST("reports")
    suspend fun create(@Body data: CreateReportRequest): Response<ReportDto>

    @PATCH("reports/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body data: UpdateReportStatusRequest
    ): Response<ReportDto>

    @DELETE("reports/{id}")
    suspend fun delete(@Path("id") id: Int): Response<MessageResponse>
}

// ═══════════════════════════════════════════════════════════════════════════
// NOTIFICATIONS SERVICE  →  /api/notifications
// ═══════════════════════════════════════════════════════════════════════════
interface NotificationsApiService {

    @GET("notifications")
    suspend fun getAll(): Response<List<NotificationDto>>

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): Response<MessageResponse>

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: Int): Response<MessageResponse>
}

// ═══════════════════════════════════════════════════════════════════════════
// SETTINGS SERVICE  →  /api/settings
// ═══════════════════════════════════════════════════════════════════════════
interface SettingsApiService {

    @GET("settings")
    suspend fun get(): Response<UserSettingsDto>

    @PUT("settings")
    suspend fun update(@Body data: UpdateSettingsRequest): Response<UserSettingsDto>

    @PUT("settings/profile")
    suspend fun updateProfile(@Body data: UpdateProfileRequest): Response<UserDto>

    @PUT("settings/password")
    suspend fun updatePassword(@Body data: UpdatePasswordRequest): Response<MessageResponse>
}

// ═══════════════════════════════════════════════════════════════════════════
// EVENTS SERVICE  →  /api/events
// ═══════════════════════════════════════════════════════════════════════════
interface EventsApiService {

    @GET("events")
    suspend fun getAll(
        @Query("month") month: String? = null
    ): Response<List<EventDto>>

    @POST("events")
    suspend fun create(@Body data: CreateEventRequest): Response<EventDto>

    @PUT("events/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body data: CreateEventRequest
    ): Response<EventDto>

    @DELETE("events/{id}")
    suspend fun delete(@Path("id") id: Int): Response<MessageResponse>
}