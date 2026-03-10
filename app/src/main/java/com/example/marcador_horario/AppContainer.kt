package com.example.marcador_horario

import android.content.Context
import com.example.marcador_horario.data.network.*
import com.example.marcador_horario.data.repository.*
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val BASE_URL = "http://10.0.2.2:3001/api/"

    val sessionManager = SessionManager(context)
    val localeManager  = AppLocaleManager(context)

    private val tokenProvider = object : TokenProvider {
        override fun getToken(): String? = runBlocking {
            sessionManager.token.firstOrNull()?.takeIf { it.isNotEmpty() }
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenProvider))
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Gson que convierte 0/1 a Boolean automáticamente
    private val gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, object : TypeAdapter<Boolean>() {
            override fun write(out: JsonWriter, value: Boolean?) {
                if (value == null) out.nullValue() else out.value(value)
            }
            override fun read(reader: JsonReader): Boolean {
                return when (reader.peek()) {
                    JsonToken.BOOLEAN -> reader.nextBoolean()
                    JsonToken.NUMBER  -> reader.nextInt() != 0
                    JsonToken.STRING  -> reader.nextString().equals("true", ignoreCase = true)
                    JsonToken.NULL    -> { reader.nextNull(); false }
                    else              -> { reader.skipValue(); false }
                }
            }
        })
        .registerTypeAdapter(java.lang.Boolean::class.java, object : TypeAdapter<Boolean>() {
            override fun write(out: JsonWriter, value: Boolean?) {
                if (value == null) out.nullValue() else out.value(value)
            }
            override fun read(reader: JsonReader): Boolean {
                return when (reader.peek()) {
                    JsonToken.BOOLEAN -> reader.nextBoolean()
                    JsonToken.NUMBER  -> reader.nextInt() != 0
                    JsonToken.STRING  -> reader.nextString().equals("true", ignoreCase = true)
                    JsonToken.NULL    -> { reader.nextNull(); false }
                    else              -> { reader.skipValue(); false }
                }
            }
        })
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val authApi          = retrofit.create(AuthApiService::class.java)
    private val attendanceApi    = retrofit.create(AttendanceApiService::class.java)
    private val reportsApi       = retrofit.create(ReportsApiService::class.java)
    private val employeesApi     = retrofit.create(EmployeesApiService::class.java)
    private val settingsApi      = retrofit.create(SettingsApiService::class.java)
    private val notificationsApi = retrofit.create(NotificationsApiService::class.java)

    val authRepository          = AuthRepository(authApi, sessionManager)
    val attendanceRepository    = AttendanceRepository(attendanceApi)
    val reportRepository        = ReportRepository(reportsApi)
    val employeesRepository     = EmployeesRepository(employeesApi)
    val settingsRepository      = SettingsRepository(settingsApi, sessionManager)
    val notificationsRepository = NotificationsRepository(notificationsApi)
}