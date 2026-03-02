package com.example.marcador_horario.ui.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    // 1. Nuestro usuario provisional
    var userName by mutableStateOf("Luis (Provisional)")

    // 2. Variables para la fecha y hora que la vista observará
    var currentDate by mutableStateOf("")
    var currentTime by mutableStateOf("")
    var currentAmPm by mutableStateOf("")

    init {
        // En cuanto arranca esta pantalla, encendemos el reloj
        startClock()
    }

    private fun startClock() {
        // viewModelScope se asegura de que este reloj se detenga si la app se cierra
        // para no gastar batería a lo tonto.
        viewModelScope.launch {
            while (true) {
                val now = Date()

                // Locale.getDefault() hace que se adapte al idioma de tu móvil.
                // Si quieres forzar inglés, usa Locale.ENGLISH
                currentDate = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)

                // "hh:mm" es formato 12 horas. Si prefieres 24h, pon "HH:mm"
                currentTime = SimpleDateFormat("hh:mm", Locale.getDefault()).format(now)

                // "a" saca el AM o PM
                currentAmPm = SimpleDateFormat("a", Locale.getDefault()).format(now).uppercase()

                // Pausamos 1 segundo (1000 milisegundos) y volvemos a actualizar
                delay(1000)
            }
        }
    }
}