package com.example.marcador_horario.ui.features.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marcador_horario.data.network.ApiResult
import com.example.marcador_horario.data.repository.AppLocaleManager
import com.example.marcador_horario.data.repository.SessionManager
import com.example.marcador_horario.data.repository.SettingsRepository
import com.example.marcador_horario.data.model.UpdateSettingsRequest
import com.example.marcador_horario.data.model.UpdateProfileRequest
import com.example.marcador_horario.data.model.UpdatePasswordRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val sessionManager: SessionManager,
    private val localeManager: AppLocaleManager
) : ViewModel() {

    // ── Perfil ────────────────────────────────────────────────────────────
    var userName    by mutableStateOf("")
    var userEmail   by mutableStateOf("")
    var userRole    by mutableStateOf("")
    var jobTitle    by mutableStateOf("")
    var avatarUrl   by mutableStateOf("")
    var location    by mutableStateOf("office")
    var department  by mutableStateOf("")

    // ── Ajustes ───────────────────────────────────────────────────────────
    var isDarkTheme        by mutableStateOf(true)
    var selectedLanguage   by mutableStateOf("English")
    var notifEnabled       by mutableStateOf(true)
    var emailCheckin       by mutableStateOf(true)
    var pushCheckin        by mutableStateOf(true)
    var gpsEnabled         by mutableStateOf(true)
    var quietHours         by mutableStateOf(true)

    // ── Contraseña ────────────────────────────────────────────────────────
    var currentPassword    by mutableStateOf("")
    var newPassword        by mutableStateOf("")
    var confirmPassword    by mutableStateOf("")

    // ── UI state ──────────────────────────────────────────────────────────
    var isLoading          by mutableStateOf(false)
    var successMessage     by mutableStateOf<String?>(null)
    var errorMessage       by mutableStateOf<String?>(null)

    val supportedLanguages = AppLocaleManager.SUPPORTED_LANGUAGES.keys.toList()

    init { loadAll() }

    private fun loadAll() {
        viewModelScope.launch {
            userName   = sessionManager.username.first()
            userEmail  = sessionManager.email.first()
            userRole   = sessionManager.role.first()
            jobTitle   = sessionManager.jobTitle.first()
            avatarUrl  = sessionManager.avatarUrl.first()
            location   = sessionManager.location.first()
            department = sessionManager.department.first()
            selectedLanguage = localeManager.savedLanguage.first()

            when (val result = settingsRepository.getSettings()) {
                is ApiResult.Success -> {
                    val s = result.data
                    isDarkTheme   = s.theme == "dark"
                    notifEnabled  = s.notifEnabled
                    emailCheckin  = s.emailCheckin
                    pushCheckin   = s.pushCheckin
                    gpsEnabled    = s.gpsEnabled
                    quietHours    = s.quietHours
                }
                else -> {}
            }
        }
    }

    fun onThemeChanged(dark: Boolean) {
        isDarkTheme = dark
        saveSettings()
    }

    fun onLanguageChanged(label: String) {
        selectedLanguage = label
        viewModelScope.launch { localeManager.changeLanguage(label) }
        saveSettings()
    }

    fun onNotifChanged(v: Boolean)      { notifEnabled = v; saveSettings() }
    fun onEmailCheckinChanged(v: Boolean) { emailCheckin = v; saveSettings() }
    fun onPushCheckinChanged(v: Boolean)  { pushCheckin = v; saveSettings() }
    fun onGpsChanged(v: Boolean)        { gpsEnabled = v; saveSettings() }
    fun onQuietHoursChanged(v: Boolean) { quietHours = v; saveSettings() }

    private fun saveSettings() {
        viewModelScope.launch {
            settingsRepository.updateSettings(
                UpdateSettingsRequest(
                    theme         = if (isDarkTheme) "dark" else "light",
                    language      = localeManager.getLanguageCode(selectedLanguage),
                    notifEnabled  = notifEnabled,
                    emailCheckin  = emailCheckin,
                    pushCheckin   = pushCheckin,
                    gpsEnabled    = gpsEnabled,
                    quietHours    = quietHours
                )
            )
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            isLoading = true
            when (val result = settingsRepository.updateProfile(
                UpdateProfileRequest(name = userName, jobTitle = jobTitle, location = location)
            )) {
                is ApiResult.Success -> successMessage = "Profile updated"
                is ApiResult.Error   -> errorMessage = result.message
                else -> {}
            }
            isLoading = false
        }
    }

    fun changePassword() {
        if (newPassword != confirmPassword) { errorMessage = "Passwords don't match"; return }
        viewModelScope.launch {
            isLoading = true
            when (val result = settingsRepository.updatePassword(currentPassword, newPassword)) {
                is ApiResult.Success -> {
                    successMessage = "Password changed"
                    currentPassword = ""; newPassword = ""; confirmPassword = ""
                }
                is ApiResult.Error -> errorMessage = result.message
                else -> {}
            }
            isLoading = false
        }
    }

    fun logout() {
        viewModelScope.launch { sessionManager.clearSession() }
    }

    fun clearMessages() { successMessage = null; errorMessage = null }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val sessionManager: SessionManager,
        private val localeManager: AppLocaleManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            SettingsViewModel(settingsRepository, sessionManager, localeManager) as T
    }
}