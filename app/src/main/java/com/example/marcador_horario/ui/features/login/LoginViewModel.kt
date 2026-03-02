package com.example.marcador_horario.ui.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // 1. Ponemos el usuario y contraseña de prueba por defecto
    var email by mutableStateOf("empleado@prueba.com")
    var password by mutableStateOf("123456")
    var passwordVisible by mutableStateOf(false)

    // Lo dejamos en 'true' para que no tengas que marcar la casilla cada vez que pruebas
    var acceptedTerms by mutableStateOf(true)

    fun onEmailChanged(newEmail: String) { email = newEmail }
    fun onPasswordChanged(newPassword: String) { password = newPassword }
    fun togglePasswordVisibility() { passwordVisible = !passwordVisible }
    fun onTermsChanged(accepted: Boolean) { acceptedTerms = accepted }

    fun onLoginClick(onSuccess: (String) -> Unit) {
        // 2. Validamos que solo deje entrar si es el usuario de prueba y aceptó términos
        if (email == "empleado@prueba.com" && password == "123456" && acceptedTerms) {

            // Recortamos el nombre (antes del @) para que el Home te salude como "Empleado"
            val extractedName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
            onSuccess(extractedName)

        } else {
            // Aquí a futuro podríamos mostrar un error si se equivocan de clave
            println("Datos incorrectos o términos no aceptados")
        }
    }
}