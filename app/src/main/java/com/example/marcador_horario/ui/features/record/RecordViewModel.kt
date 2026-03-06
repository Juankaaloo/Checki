package com.example.marcador_horario.ui.features.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Calendar

// ─── Modelo de datos para un registro de fichaje ─────────────────────────────
data class WorkRecord(
    val dateLabel: String,       // Ej: "Monday, March 3"
    val entrance: String,        // Ej: "08:30 AM"
    val exit: String,            // Ej: "17:00 PM"
    val timeElapsed: String,     // Ej: "08h 30m"
    val workedMinutes: Int,      // Para cálculos y color
    val location: String         // "Office" | "Home"
)

class RecordViewModel : ViewModel() {

    // ─── Mes y año mostrados actualmente ─────────────────────────────────────
    var displayedMonth by mutableStateOf(Calendar.getInstance().get(Calendar.MONTH))
        private set
    var displayedYear by mutableStateOf(Calendar.getInstance().get(Calendar.YEAR))
        private set

    // ─── Nombre del mes formateado ────────────────────────────────────────────
    val monthLabel: String
        get() {
            val months = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            return "${months[displayedMonth]} $displayedYear"
        }

    // ─── Datos simulados (en una app real vendrían de Room/DB) ────────────────
    private val allRecords: Map<Pair<Int, Int>, List<WorkRecord>> = mapOf(
        // Enero 2025
        Pair(Calendar.JANUARY, 2025) to listOf(
            WorkRecord("Wednesday, January 8",  "08:00 AM", "16:30 PM", "08h 30m", 510, "Office"),
            WorkRecord("Thursday, January 9",   "09:00 AM", "18:00 PM", "09h 00m", 540, "Home"),
            WorkRecord("Friday, January 10",    "08:30 AM", "14:00 PM", "05h 30m", 330, "Office"),
            WorkRecord("Monday, January 13",    "08:00 AM", "16:00 PM", "08h 00m", 480, "Office"),
            WorkRecord("Tuesday, January 14",   "10:00 AM", "19:00 PM", "09h 00m", 540, "Home"),
        ),
        // Febrero 2025
        Pair(Calendar.FEBRUARY, 2025) to listOf(
            WorkRecord("Monday, February 3",    "08:00 AM", "16:00 PM", "07h 00m", 420, "Office"),
            WorkRecord("Tuesday, February 4",   "09:30 AM", "18:30 PM", "09h 00m", 540, "Home"),
            WorkRecord("Wednesday, February 5", "08:00 AM", "17:00 PM", "09h 00m", 540, "Office"),
            WorkRecord("Thursday, February 6",  "10:00 AM", "14:00 PM", "04h 00m", 240, "Home"),
            WorkRecord("Friday, February 7",    "08:00 AM", "16:00 PM", "08h 00m", 480, "Office"),
            WorkRecord("Monday, February 10",   "09:00 AM", "17:30 PM", "08h 30m", 510, "Home"),
            WorkRecord("Tuesday, February 11",  "08:00 AM", "16:00 PM", "08h 00m", 480, "Office"),
        ),
        // Marzo 2025
        Pair(Calendar.MARCH, 2025) to listOf(
            WorkRecord("Monday, March 3",       "08:15 AM", "17:00 PM", "08h 45m", 525, "Office"),
            WorkRecord("Tuesday, March 4",      "09:00 AM", "18:00 PM", "09h 00m", 540, "Home"),
            WorkRecord("Wednesday, March 5",    "08:00 AM", "16:30 PM", "08h 30m", 510, "Office"),
            WorkRecord("Thursday, March 6",     "10:30 AM", "19:30 PM", "09h 00m", 540, "Home"),
            WorkRecord("Friday, March 7",       "08:00 AM", "13:00 PM", "05h 00m", 300, "Office"),
        ),
        // Abril 2025
        Pair(Calendar.APRIL, 2025) to listOf(
            WorkRecord("Tuesday, April 1",      "08:00 AM", "17:00 PM", "09h 00m", 540, "Office"),
            WorkRecord("Wednesday, April 2",    "09:00 AM", "17:30 PM", "08h 30m", 510, "Home"),
        ),
        // Mayo 2025 — sin registros (para mostrar pantalla vacía)
        // Junio 2025
        Pair(Calendar.JUNE, 2025) to listOf(
            WorkRecord("Monday, June 2",        "08:00 AM", "16:00 PM", "08h 00m", 480, "Office"),
        )
    )

    // ─── Registros del mes mostrado actualmente ───────────────────────────────
    val currentRecords: List<WorkRecord>
        get() = allRecords[Pair(displayedMonth, displayedYear)] ?: emptyList()

    // ─── Total de horas trabajadas en el mes ──────────────────────────────────
    val monthlySummary: String
        get() {
            val totalMinutes = currentRecords.sumOf { it.workedMinutes }
            val hours = totalMinutes / 60
            val mins  = totalMinutes % 60
            return "%02dh %02dm".format(hours, mins)
        }

    val monthlyDays: Int
        get() = currentRecords.size

    // ─── Navegación de mes ────────────────────────────────────────────────────
    fun previousMonth() {
        if (displayedMonth == Calendar.JANUARY) {
            displayedMonth = Calendar.DECEMBER
            displayedYear--
        } else {
            displayedMonth--
        }
    }

    fun nextMonth() {
        val now = Calendar.getInstance()
        // No permitir navegar más allá del mes actual
        if (displayedYear == now.get(Calendar.YEAR) && displayedMonth == now.get(Calendar.MONTH)) return
        if (displayedMonth == Calendar.DECEMBER) {
            displayedMonth = Calendar.JANUARY
            displayedYear++
        } else {
            displayedMonth++
        }
    }

    /** True si ya estamos en el mes actual (no se puede avanzar más) */
    val isCurrentMonth: Boolean
        get() {
            val now = Calendar.getInstance()
            return displayedYear == now.get(Calendar.YEAR) && displayedMonth == now.get(Calendar.MONTH)
        }

    // ─── Color de barra lateral según minutos trabajados ─────────────────────
    // < 6h → rojo, 6h–7h59m → naranja, ≥ 8h → verde
    fun barColorForMinutes(minutes: Int): Long = when {
        minutes >= 480 -> 0xFF00A313  // verde  (≥ 8h)
        minutes >= 360 -> 0xFFF2994A  // naranja (6h–8h)
        else           -> 0xFFFF5252  // rojo   (< 6h)
    }
}