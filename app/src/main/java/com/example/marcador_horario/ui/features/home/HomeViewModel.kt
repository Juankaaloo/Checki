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
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    // ─── Datos de usuario y fecha/hora ───────────────────────────────────────
    var userName by mutableStateOf("")
    var currentTime by mutableStateOf("")
    var currentAmPm by mutableStateOf("")
    var currentDate by mutableStateOf("")

    // ─── Estado de fichaje ────────────────────────────────────────────────────
    var isPunchedIn by mutableStateOf(false)
    var isOnBreak by mutableStateOf(false)
    var jobLocation by mutableStateOf("Office")

    var entranceTime by mutableStateOf("--:--")
    var exitTime by mutableStateOf("--:--")

    //  Tiempo transcurrido trabajando (sin contar descansos)
    var timeElapsed by mutableStateOf("00:00:00")

    // ─── Nuevas propiedades ───────────────────────────────────────────────────
    /** Progreso 0f..1f hacia las 8 horas objetivo */
    var workProgress by mutableStateOf(0f)

    /** Tiempo total trabajado en segundos (sin contar descansos activos) */
    var workedSeconds by mutableStateOf(0L)

    /** Duración del descanso actual en tiempo real */
    var breakDuration by mutableStateOf("00:00")

    /**
     * Estado legible para el chip:
     * "Not Clocked In" | "Working" | "On Break"
     */
    var workStatus by mutableStateOf("Not Clocked In")

    // ─── Interno ──────────────────────────────────────────────────────────────
    private var clockJob: Job? = null
    private var workTimerJob: Job? = null
    private var breakTimerJob: Job? = null
    private var breakSeconds = 0L
    private var notificationSentAt8h = false
    private var appContext: Context? = null

    init {
        startClock()
    }

    /** Llamar desde la Activity/Composable para que las notificaciones funcionen */
    fun attachContext(context: Context) {
        appContext = context.applicationContext
        createNotificationChannel()
    }

    // ─── Reloj en tiempo real ─────────────────────────────────────────────────
    private fun startClock() {
        clockJob = viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                val timeFmt = SimpleDateFormat("hh:mm", Locale.getDefault())
                val amPmFmt = SimpleDateFormat("a", Locale.getDefault())
                val dateFmt = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
                currentTime = timeFmt.format(cal.time)
                currentAmPm = amPmFmt.format(cal.time)
                currentDate = dateFmt.format(cal.time)
                delay(1000)
            }
        }
    }

    // ─── Marcar entrada / salida ──────────────────────────────────────────────
    fun onMarkEntryClick() {
        if (!isPunchedIn) {
            // ENTRADA
            isPunchedIn = true
            workStatus = "Working"
            val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
            entranceTime = fmt.format(Date())
            exitTime = "--:--"
            workedSeconds = 0L
            notificationSentAt8h = false
            startWorkTimer()
        } else {
            // SALIDA
            if (isOnBreak) stopBreakTimer()
            isPunchedIn = false
            isOnBreak = false
            workStatus = "Not Clocked In"
            val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
            exitTime = fmt.format(Date())
            stopWorkTimer()
        }
    }

    // ─── Descanso ─────────────────────────────────────────────────────────────
    fun toggleBreak() {
        if (!isOnBreak) {
            isOnBreak = true
            workStatus = "On Break"
            breakSeconds = 0L
            stopWorkTimer()   // pausar contador de trabajo
            startBreakTimer()
        } else {
            isOnBreak = false
            workStatus = "Working"
            stopBreakTimer()
            startWorkTimer()  // reanudar contador de trabajo
        }
    }

    // ─── Timer de trabajo ─────────────────────────────────────────────────────
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

    private fun stopWorkTimer() {
        workTimerJob?.cancel()
        workTimerJob = null
    }

    // ─── Timer de descanso ────────────────────────────────────────────────────
    private fun startBreakTimer() {
        breakTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                breakSeconds++
                val mins = breakSeconds / 60
                val secs = breakSeconds % 60
                breakDuration = "%02d:%02d".format(mins, secs)
            }
        }
    }

    private fun stopBreakTimer() {
        breakTimerJob?.cancel()
        breakTimerJob = null
        breakDuration = "00:00"
    }

    // ─── Notificación a las 8 horas ───────────────────────────────────────────
    private fun checkEightHourNotification() {
        if (!notificationSentAt8h && workedSeconds >= 8 * 3600) {
            notificationSentAt8h = true
            sendEightHourNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Work Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Reminders about your work shift" }
            val manager = appContext?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun sendEightHourNotification() {
        val context = appContext ?: return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.checkiii)
            .setContentTitle("8 Hours Reached 🎉")
            .setContentText("You've completed your 8-hour workday. Consider wrapping up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private fun formatSeconds(total: Long): String {
        val h = total / 3600
        val m = (total % 3600) / 60
        val s = total % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    override fun onCleared() {
        super.onCleared()
        clockJob?.cancel()
        stopWorkTimer()
        stopBreakTimer()
    }

    companion object {
        private const val CHANNEL_ID = "work_reminders"
        private const val NOTIFICATION_ID = 1001
    }
}