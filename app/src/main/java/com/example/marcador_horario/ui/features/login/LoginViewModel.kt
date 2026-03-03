package com.example.marcador_horario.ui.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel que gestiona la lógica de autenticación y los campos del formulario de login.
 */
class LoginViewModel : ViewModel() {

    // Variables de estado atadas directamente a los campos de texto de la UI (Two-way data binding)
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var acceptedTerms by mutableStateOf(true)

    // Funciones de actualización de estado llamadas desde los campos de texto
    fun onEmailChanged(newEmail: String) { email = newEmail }
    fun onPasswordChanged(newPassword: String) { password = newPassword }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun onTermsChanged(accepted: Boolean) { acceptedTerms = accepted }

    /**
     * Simula una llamada a backend para validar las credenciales.
     * @param onSuccess Callback que retorna el nombre formateado y un Boolean (true = es Admin).
     */
    fun onLoginClick(onSuccess: (String, Boolean) -> Unit) {
        // Validación básica de formulario local
        if (email.isNotEmpty() && password.isNotEmpty() && acceptedTerms) {

            // Lógica de mock (simulación).
            // TODO (Fase 2): Reemplazar por autenticación real con Firebase o API REST.
            if (email == "admin@prueba.com" && password == "admin123") {
                // Credenciales maestras: Acceso al panel de administrador
                onSuccess("Administrador", true)
            } else {
                // Resto de credenciales: Acceso estándar de empleado
                // Extraemos el nombre de la dirección de correo y capitalizamos la primera letra
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                onSuccess(name, false)
            }
        }
    }
}