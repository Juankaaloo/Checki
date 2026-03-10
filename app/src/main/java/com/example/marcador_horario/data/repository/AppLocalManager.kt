package com.example.marcador_horario.data.repository

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.langDataStore by preferencesDataStore(name = "language_prefs")

/**
 * Gestiona el cambio de idioma compatible con minSdk 24.
 * Sin Hilt — se instancia manualmente pasando el Context de la app.
 */
class AppLocaleManager(private val context: Context) {

    companion object {
        private val KEY_LANGUAGE = stringPreferencesKey("selected_language")

        val SUPPORTED_LANGUAGES = linkedMapOf(
            "English"  to "en",
            "Español"  to "es",
            "Français" to "fr",
            "Deutsch"  to "de"
        )
    }

    // ── Cambiar idioma + persistir ────────────────────────────────────────
    suspend fun changeLanguage(languageLabel: String) {
        val code = SUPPORTED_LANGUAGES[languageLabel] ?: "en"
        applyLocale(code)
        context.langDataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = languageLabel
        }
    }

    // ── Restaurar idioma guardado ─────────────────────────────────────────
    suspend fun restoreLanguage(savedLabel: String) {
        val code = SUPPORTED_LANGUAGES[savedLabel] ?: return
        applyLocale(code)
    }

    // ── Aplicar locale al contexto (compatible API 24+) ───────────────────
    private fun applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getLanguageCode(label: String): String =
        SUPPORTED_LANGUAGES[label] ?: "en"

    val savedLanguage: Flow<String> = context.langDataStore.data
        .map { it[KEY_LANGUAGE] ?: "English" }
}