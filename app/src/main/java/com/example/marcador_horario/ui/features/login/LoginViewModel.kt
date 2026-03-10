package com.example.marcador_horario.ui.features.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email           by mutableStateOf("")
    var password        by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var acceptedTerms   by mutableStateOf(true)
    var isLoading       by mutableStateOf(false)
    var errorMessage    by mutableStateOf<String?>(null)

    fun onEmailChanged(v: String)    { email = v; errorMessage = null }
    fun onPasswordChanged(v: String) { password = v; errorMessage = null }
    fun togglePasswordVisibility()   { passwordVisible = !passwordVisible }
    fun onTermsChanged(v: Boolean)   { acceptedTerms = v }

    fun onLoginClick(onSuccess: (String, Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            when (val result = authRepository.login(email.trim(), password)) {
                is ApiResult.Success -> {
                    val user = result.data
                    onSuccess(user.name, user.isAdmin)
                }
                is ApiResult.Error -> {
                    errorMessage = result.message
                }
                else -> {}
            }
            isLoading = false
        }
    }

    class Factory(private val repo: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            LoginViewModel(repo) as T
    }
}