package com.example.marcador_horario.ui.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // Lo dejamos vacío por defecto para que tú escribas el que quieras probar
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var acceptedTerms by mutableStateOf(true)

    fun onEmailChanged(newEmail: String) { email = newEmail }
    fun onPasswordChanged(newPassword: String) { password = newPassword }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun onTermsChanged(accepted: Boolean) { acceptedTerms = accepted }

    fun onLoginClick(onSuccess: (String, Boolean) -> Unit) {
        // Solo verificamos que no estén vacíos y que haya aceptado términos
        if (email.isNotEmpty() && password.isNotEmpty() && acceptedTerms) {

            if (email == "admin@prueba.com" && password == "admin123") {
                // 1. Si escribe exactamente esto, va a la pantalla de ADMIN
                onSuccess("Administrador", true)
            } else {
                // 2. CUALQUIER OTRO CORREO entra como empleado normal al HOME
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                onSuccess(name, false)
            }

        }
    }
}