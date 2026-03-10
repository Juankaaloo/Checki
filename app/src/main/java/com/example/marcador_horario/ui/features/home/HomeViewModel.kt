package com.example.marcador_horario.ui.features.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.R
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.AttendanceRepository
import com.example.marcador_horario.data.repository.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var userName      by mutableStateOf("")
    var currentTime   by mutableStateOf("")
    var currentAmPm   by mutableStateOf("")
    var currentDate   by mutableStateOf("")
    var isPunchedIn   by mutableStateOf(false)
    var isOnBreak     by mutableStateOf(false)
    var jobLocation   by mutableStateOf("Office")
    var entranceTime  by mutableStateOf("--:--")
    var exitTime      by mutableStateOf("--:--")
    var timeElapsed   by mutableStateOf("00:00:00")
    var workProgress  by mutableStateOf(0f)
    var workedSeconds by mutableStateOf(0L)
    var breakDuration by mutableStateOf("00:00")
    var workStatus    by mutableStateOf("Not Clocked In")
    var errorMessage  by mutableStateOf<String?>(null)

    private var clockJob: Job? = null
    private var workTimerJob: Job? = null
    private var breakTimerJob: Job? = null
    private var breakSeconds = 0L
    private var notificationSentAt8h = false
    private var appContext: Context? = null
    private var attendanceId: Int? = null

    init {
        startClock()
        loadSession()
        loadTodayRecord()
    }

    fun attachContext(context: Context) {
        appContext = context.applicationContext
        createNotificationChannel()
    }

    private fun loadSession() {
        viewModelScope.launch {
            userName = sessionManager.username.first()
        }
    }

    private fun loadTodayRecord() {
        viewModelScope.launch {
            when (val result = attendanceRepository.getToday()) {
                is ApiResult.Success -> {
                    val record = result.data
                    attendanceId = record.id
                    isPunchedIn = record.isClockedIn && !record.isClockedOut
                    isOnBreak = record.isOnBreak
                    workStatus = when {
                        record.isOnBreak    -> "On Break"
                        record.isClockedIn  -> "Working"
                        else                -> "Not Clocked In"
                    }
                    record.checkIn?.let {
                        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it)
                        entranceTime = date?.let { d -> fmt.format(d) } ?: "--:--"
                    }
                    record.checkOut?.let {
                        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it)
                        exitTime = date?.let { d -> fmt.format(d) } ?: "--:--"
                    }
                    workedSeconds = (record.workedMinutes * 60).toLong()
                    timeElapsed = formatSeconds(workedSeconds)
                    workProgress = (workedSeconds / (8 * 3600f)).coerceIn(0f, 1f)
                    jobLocation = record.location.replaceFirstChar { it.uppercase() }
                    if (isPunchedIn && !isOnBreak) startWorkTimer()
                    if (isOnBreak) startBreakTimer()
                }
                is ApiResult.Error -> { /* sin registro hoy, estado inicial */ }
                else -> {}
            }
        }
    }

    fun onMarkEntryClick() {
        viewModelScope.launch {
            if (!isPunchedIn) {
                when (val result = attendanceRepository.checkIn(jobLocation.lowercase())) {
                    is ApiResult.Success -> {
                        attendanceId = result.data.id
                        isPunchedIn = true
                        workStatus = "Working"
                        entranceTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        exitTime = "--:--"
                        workedSeconds = 0L
                        notificationSentAt8h = false
                        startWorkTimer()
                    }
                    is ApiResult.Error -> errorMessage = result.message
                    else -> {}
                }
            } else {
                if (isOnBreak) {
                    attendanceRepository.endBreak()
                    stopBreakTimer()
                }
                when (val result = attendanceRepository.checkOut()) {
                    is ApiResult.Success -> {
                        isPunchedIn = false
                        isOnBreak = false
                        workStatus = "Not Clocked In"
                        exitTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        stopWorkTimer()
                    }
                    is ApiResult.Error -> errorMessage = result.message
                    else -> {}
                }
            }
        }
    }

    fun toggleBreak() {
        viewModelScope.launch {
            if (!isOnBreak) {
                when (val result = attendanceRepository.startBreak()) {
                    is ApiResult.Success -> {
                        isOnBreak = true
                        workStatus = "On Break"
                        breakSeconds = 0L
                        stopWorkTimer()
                        startBreakTimer()
                    }
                    is ApiResult.Error -> errorMessage = result.message
                    else -> {}
                }
            } else {
                when (val result = attendanceRepository.endBreak()) {
                    is ApiResult.Success -> {
                        isOnBreak = false
                        workStatus = "Working"
                        stopBreakTimer()
                        startWorkTimer()
                    }
                    is ApiResult.Error -> errorMessage = result.message
                    else -> {}
                }
            }
        }
    }

    private fun startClock() {
        clockJob = viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                currentTime = SimpleDateFormat("hh:mm", Locale.getDefault()).format(cal.time)
                currentAmPm = SimpleDateFormat("a", Locale.getDefault()).format(cal.time)
                currentDate = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(cal.time)
                delay(1000)
            }
        }
    }

    private fun startWorkTimer() {
        workTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                workedSeconds++
                timeElapsed = formatSeconds(workedSeconds)
                workProgress = (workedSeconds / (8 * 3600f)).coerceIn(0f, 1f)
                checkEightHourNotification()
            }
        }
    }

    private fun stopWorkTimer() { workTimerJob?.cancel(); workTimerJob = null }

    private fun startBreakTimer() {
        breakTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                breakSeconds++
                breakDuration = "%02d:%02d".format(breakSeconds / 60, breakSeconds % 60)
            }
        }
    }

    private fun stopBreakTimer() {
        breakTimerJob?.cancel(); breakTimerJob = null; breakDuration = "00:00"
    }

    private fun checkEightHourNotification() {
        if (!notificationSentAt8h && workedSeconds >= 8 * 3600) {
            notificationSentAt8h = true
            sendEightHourNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Work Reminders", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Reminders about your work shift" }
            (appContext?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)
                ?.createNotificationChannel(channel)
        }
    }

    private fun sendEightHourNotification() {
        val ctx = appContext ?: return
        val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.checkiii)
            .setContentTitle("8 Hours Reached 🎉")
            .setContentText("You've completed your 8-hour workday. Consider wrapping up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true).build()
        (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.notify(NOTIFICATION_ID, n)
    }

    private fun formatSeconds(total: Long): String {
        val h = total / 3600; val m = (total % 3600) / 60; val s = total % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel(); stopWorkTimer(); stopBreakTimer()
    }

    companion object {
        private const val CHANNEL_ID = "work_reminders"
        private const val NOTIFICATION_ID = 1001
    }

    class Factory(
        private val attendanceRepository: AttendanceRepository,
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            HomeViewModel(attendanceRepository, sessionManager) as T
    }
}