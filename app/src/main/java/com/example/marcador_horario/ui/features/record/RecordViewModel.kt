package com.example.marcador_horario.ui.features.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.model.AttendanceDto
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.AttendanceRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class WorkRecord(
    val dateLabel: String,
    val entrance: String,
    val exit: String,
    val timeElapsed: String,
    val workedMinutes: Int,
    val location: String
)

class RecordViewModel(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    var displayedMonth by mutableStateOf(Calendar.getInstance().get(Calendar.MONTH))
        private set
    var displayedYear by mutableStateOf(Calendar.getInstance().get(Calendar.YEAR))
        private set

    var currentRecords by mutableStateOf<List<WorkRecord>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val monthLabel: String
        get() {
            val months = listOf("January","February","March","April","May","June",
                "July","August","September","October","November","December")
            return "${months[displayedMonth]} $displayedYear"
        }

    val monthlySummary: String
        get() {
            val total = currentRecords.sumOf { it.workedMinutes }
            return "%02dh %02dm".format(total / 60, total % 60)
        }

    val monthlyDays: Int get() = currentRecords.size

    val isCurrentMonth: Boolean
        get() {
            val now = Calendar.getInstance()
            return displayedYear == now.get(Calendar.YEAR) && displayedMonth == now.get(Calendar.MONTH)
        }

    init { loadCurrentMonth() }

    private fun loadCurrentMonth() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val monthStr = "%04d-%02d".format(displayedYear, displayedMonth + 1)
            when (val result = attendanceRepository.getHistory(month = monthStr)) {
                is ApiResult.Success -> currentRecords = result.data.records.map { it.toWorkRecord() }
                is ApiResult.Error   -> errorMessage = result.message
                else -> {}
            }
            isLoading = false
        }
    }

    fun previousMonth() {
        if (displayedMonth == Calendar.JANUARY) { displayedMonth = Calendar.DECEMBER; displayedYear-- }
        else displayedMonth--
        loadCurrentMonth()
    }

    fun nextMonth() {
        val now = Calendar.getInstance()
        if (displayedYear == now.get(Calendar.YEAR) && displayedMonth == now.get(Calendar.MONTH)) return
        if (displayedMonth == Calendar.DECEMBER) { displayedMonth = Calendar.JANUARY; displayedYear++ }
        else displayedMonth++
        loadCurrentMonth()
    }

    fun barColorForMinutes(minutes: Int): Long = when {
        minutes >= 480 -> 0xFF00A313
        minutes >= 360 -> 0xFFF2994A
        else           -> 0xFFFF5252
    }

    class Factory(private val repo: AttendanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = RecordViewModel(repo) as T
    }
}

private fun AttendanceDto.toWorkRecord(): WorkRecord {
    val dtFmt  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val timeFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFmt = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

    val checkInDate  = checkIn?.let  { runCatching { dtFmt.parse(it) }.getOrNull() }
    val checkOutDate = checkOut?.let { runCatching { dtFmt.parse(it) }.getOrNull() }

    val dateLabel  = checkInDate?.let { dateFmt.format(it) } ?: date
    val entryStr   = checkInDate?.let  { timeFmt.format(it) } ?: "--:--"
    val exitStr    = checkOutDate?.let { timeFmt.format(it) } ?: "--:--"
    val elapsed    = "%02dh %02dm".format(workedMinutes / 60, workedMinutes % 60)
    val loc        = location.replaceFirstChar { it.uppercase() }

    return WorkRecord(dateLabel, entryStr, exitStr, elapsed, workedMinutes, loc)
}