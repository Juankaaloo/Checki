package com.example.marcador_horario.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.marcador_horario.data.model.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

/**
 * Gestiona la sesión del usuario usando DataStore.
 * Sin Hilt — se instancia manualmente pasando el Context de la app.
 */
class SessionManager(private val context: Context) {

    companion object {
        val KEY_TOKEN      = stringPreferencesKey("jwt_token")
        val KEY_USER_ID    = intPreferencesKey("user_id")
        val KEY_USERNAME   = stringPreferencesKey("username")
        val KEY_EMAIL      = stringPreferencesKey("email")
        val KEY_IS_ADMIN   = booleanPreferencesKey("is_admin")
        val KEY_IS_LOGGED  = booleanPreferencesKey("is_logged_in")
        val KEY_ROLE       = stringPreferencesKey("role")
        val KEY_JOB_TITLE  = stringPreferencesKey("job_title")
        val KEY_AVATAR_URL = stringPreferencesKey("avatar_url")
        val KEY_LOCATION   = stringPreferencesKey("location")
        val KEY_DEPT       = stringPreferencesKey("department")
    }

    suspend fun saveSession(token: String, user: UserDto) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN]      = token
            prefs[KEY_USER_ID]    = user.id
            prefs[KEY_USERNAME]   = user.name
            prefs[KEY_EMAIL]      = user.email
            prefs[KEY_IS_ADMIN]   = user.isAdmin
            prefs[KEY_IS_LOGGED]  = true
            prefs[KEY_ROLE]       = user.role
            prefs[KEY_JOB_TITLE]  = user.jobTitle  ?: ""
            prefs[KEY_AVATAR_URL] = user.avatarUrl ?: ""
            prefs[KEY_LOCATION]   = user.location
            prefs[KEY_DEPT]       = user.department ?: ""
        }
    }

    suspend fun updateProfile(user: UserDto) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USERNAME]   = user.name
            prefs[KEY_EMAIL]      = user.email
            prefs[KEY_JOB_TITLE]  = user.jobTitle  ?: ""
            prefs[KEY_AVATAR_URL] = user.avatarUrl ?: ""
            prefs[KEY_LOCATION]   = user.location
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }

    val token:      Flow<String>  = context.dataStore.data.map { it[KEY_TOKEN]      ?: "" }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_LOGGED]  ?: false }
    val userId:     Flow<Int>     = context.dataStore.data.map { it[KEY_USER_ID]    ?: -1 }
    val username:   Flow<String>  = context.dataStore.data.map { it[KEY_USERNAME]   ?: "" }
    val email:      Flow<String>  = context.dataStore.data.map { it[KEY_EMAIL]      ?: "" }
    val isAdmin:    Flow<Boolean> = context.dataStore.data.map { it[KEY_IS_ADMIN]   ?: false }
    val role:       Flow<String>  = context.dataStore.data.map { it[KEY_ROLE]       ?: "employee" }
    val jobTitle:   Flow<String>  = context.dataStore.data.map { it[KEY_JOB_TITLE]  ?: "" }
    val avatarUrl:  Flow<String>  = context.dataStore.data.map { it[KEY_AVATAR_URL] ?: "" }
    val location:   Flow<String>  = context.dataStore.data.map { it[KEY_LOCATION]   ?: "office" }
    val department: Flow<String>  = context.dataStore.data.map { it[KEY_DEPT]       ?: "" }
}