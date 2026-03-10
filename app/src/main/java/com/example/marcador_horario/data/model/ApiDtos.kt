package com.example.marcador_horario.data.model

import com.google.gson.annotations.SerializedName

// ═══════════════════════════════════════════════════════════════════════════
// AUTH
// ═══════════════════════════════════════════════════════════════════════════

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    @SerializedName("job_title") val jobTitle: String? = null,
    val role: String = "employee"
)

// ═══════════════════════════════════════════════════════════════════════════
// USER / EMPLOYEE
// ═══════════════════════════════════════════════════════════════════════════

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,                           // "admin" | "employee"
    @SerializedName("job_title")  val jobTitle: String? = null,
    @SerializedName("dept_id")    val deptId: Int? = null,
    val phone: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val location: String = "office",            // "office" | "home" | "remote"
    @SerializedName("shift_start") val shiftStart: String? = null,
    @SerializedName("shift_end")   val shiftEnd: String? = null,
    @SerializedName("join_date")   val joinDate: String? = null,
    @SerializedName("is_active")   val isActive: Boolean = true,
    @SerializedName("created_at")  val createdAt: String? = null,
    // Campo calculado que puede devolver la API en /employees
    val department: String? = null
) {
    val isAdmin: Boolean get() = role == "admin"
}

data class EmployeeUpdateRequest(
    val name: String? = null,
    val email: String? = null,
    @SerializedName("job_title")  val jobTitle: String? = null,
    @SerializedName("dept_id")    val deptId: Int? = null,
    val phone: String? = null,
    val location: String? = null,
    @SerializedName("shift_start") val shiftStart: String? = null,
    @SerializedName("shift_end")   val shiftEnd: String? = null,
    @SerializedName("is_active")   val isActive: Boolean? = null
)

data class DepartmentStatsDto(
    val department: String,
    val total: Int,
    val active: Int
)

// ═══════════════════════════════════════════════════════════════════════════
// ATTENDANCE
// ═══════════════════════════════════════════════════════════════════════════

data class CheckInRequest(
    val location: String = "office"             // "office" | "home" | "remote"
)

data class AttendanceDto(
    val id: Int,
    @SerializedName("user_id")       val userId: Int,
    val date: String,                           // "2025-06-10"
    @SerializedName("check_in")      val checkIn: String? = null,   // "2025-06-10T08:00:00"
    @SerializedName("check_out")     val checkOut: String? = null,
    val status: String = "on_time",             // "on_time" | "late" | "absent" | "on_leave"
    val location: String = "office",
    @SerializedName("worked_minutes") val workedMinutes: Int = 0,
    val notes: String? = null,
    // Breaks anidados (puede devolver la API en /today)
    val breaks: List<BreakDto> = emptyList(),
    // Campo calculado: si hay un break activo (sin end_time)
    val activeBreak: BreakDto? = null
) {
    val isClockedIn:  Boolean get() = checkIn != null
    val isClockedOut: Boolean get() = checkOut != null
    val isOnBreak:    Boolean get() = activeBreak != null
}

data class BreakDto(
    val id: Int,
    @SerializedName("attendance_id")    val attendanceId: Int,
    @SerializedName("start_time")       val startTime: String,
    @SerializedName("end_time")         val endTime: String? = null,
    @SerializedName("duration_minutes") val durationMinutes: Int = 0
)

data class WeeklyStatsDto(
    @SerializedName("total_days")    val totalDays: Int = 0,
    @SerializedName("present_days")  val presentDays: Int = 0,
    @SerializedName("absent_days")   val absentDays: Int = 0,
    @SerializedName("late_days")     val lateDays: Int = 0,
    @SerializedName("total_minutes") val totalMinutes: Int = 0
)

data class AttendanceHistoryDto(
    val records: List<AttendanceDto> = emptyList(),
    val total: Int = 0
)

// ═══════════════════════════════════════════════════════════════════════════
// REPORTS
// ═══════════════════════════════════════════════════════════════════════════

data class ReportDto(
    val id: Int,
    @SerializedName("user_id")      val userId: Int,
    val type: String,                           // "vacation"|"medical"|"incident"|"clock_issue"|"system_error"|"other"
    val reason: String? = null,
    val description: String? = null,
    val date: String,
    val status: String = "pending",             // "pending"|"approved"|"rejected"|"resolved"
    @SerializedName("reviewed_by")  val reviewedBy: Int? = null,
    @SerializedName("reviewed_at")  val reviewedAt: String? = null,
    @SerializedName("created_at")   val createdAt: String? = null,
    // Campos enriquecidos que puede devolver la API (JOIN con users)
    @SerializedName("employee_name") val employeeName: String? = null,
    val department: String? = null
)

data class CreateReportRequest(
    val type: String,
    val reason: String? = null,
    val description: String,
    val date: String                            // "yyyy-MM-dd"
)

data class UpdateReportStatusRequest(
    val status: String                          // "approved" | "rejected" | "resolved"
)

data class ReportStatsDto(
    val pending: Int = 0,
    val approved: Int = 0,
    val rejected: Int = 0,
    val resolved: Int = 0,
    val total: Int = 0
)

data class ReportsListDto(
    val reports: List<ReportDto> = emptyList(),
    val total: Int = 0
)

// ═══════════════════════════════════════════════════════════════════════════
// NOTIFICATIONS
// ═══════════════════════════════════════════════════════════════════════════

data class NotificationDto(
    val id: Int,
    @SerializedName("user_id")    val userId: Int,
    val title: String,
    val description: String? = null,
    val type: String = "info",                  // "info"|"success"|"alert"|"error"|"request"|"incident"
    @SerializedName("is_read")    val isRead: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null
)

// ═══════════════════════════════════════════════════════════════════════════
// SETTINGS
// ═══════════════════════════════════════════════════════════════════════════

data class UserSettingsDto(
    val theme: String = "dark",                 // "light"|"dark"|"system"
    val language: String = "en",
    val timezone: String = "Europe/Madrid",
    @SerializedName("notif_enabled")  val notifEnabled: Boolean = true,
    @SerializedName("email_checkin")  val emailCheckin: Boolean = true,
    @SerializedName("email_late")     val emailLate: Boolean = true,
    @SerializedName("push_checkin")   val pushCheckin: Boolean = true,
    @SerializedName("gps_enabled")    val gpsEnabled: Boolean = true,
    @SerializedName("quiet_hours")    val quietHours: Boolean = true,
    @SerializedName("time_format")    val timeFormat: String = "24h"
)

data class UpdateSettingsRequest(
    val theme: String? = null,
    val language: String? = null,
    @SerializedName("notif_enabled") val notifEnabled: Boolean? = null,
    @SerializedName("email_checkin") val emailCheckin: Boolean? = null,
    @SerializedName("push_checkin")  val pushCheckin: Boolean? = null,
    @SerializedName("gps_enabled")   val gpsEnabled: Boolean? = null,
    @SerializedName("quiet_hours")   val quietHours: Boolean? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    @SerializedName("job_title") val jobTitle: String? = null,
    val location: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class UpdatePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password")     val newPassword: String
)

// ═══════════════════════════════════════════════════════════════════════════
// EVENTOS
// ═══════════════════════════════════════════════════════════════════════════

data class EventDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val title: String,
    val date: String,
    val time: String? = null,
    val type: String = "meeting"                // "meeting"|"deadline"|"leave"|"other"
)

data class CreateEventRequest(
    val title: String,
    val date: String,
    val time: String? = null,
    val type: String = "meeting"
)

// ═══════════════════════════════════════════════════════════════════════════
// GENÉRICO
// ═══════════════════════════════════════════════════════════════════════════

data class MessageResponse(
    val message: String
)

data class ApiError(
    val error: String,
    val status: Int? = null
)