package com.example.marcador_horario.data.network

import retrofit2.Response

/**
 * Wrapper sellado para resultados de la API.
 * Reemplaza los sealed class anteriores (AuthResult, RecordResult, etc.)
 * con uno genérico que funciona para todos los repositorios.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T)   : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading                        : ApiResult<Nothing>()
}

/**
 * Extensión para convertir Response<T> de Retrofit en ApiResult<T>.
 * Maneja automáticamente errores HTTP y excepciones de red.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Respuesta vacía del servidor", response.code())
            }
        } else {
            val errorMsg = when (response.code()) {
                400 -> "Datos incorrectos"
                401 -> "No autorizado — sesión expirada"
                403 -> "Sin permisos para esta acción"
                404 -> "Recurso no encontrado"
                409 -> "El recurso ya existe"
                500 -> "Error interno del servidor"
                else -> "Error ${response.code()}"
            }
            ApiResult.Error(errorMsg, response.code())
        }
    } catch (e: java.net.UnknownHostException) {
        ApiResult.Error("Sin conexión — verifica tu red")
    } catch (e: java.net.SocketTimeoutException) {
        ApiResult.Error("Tiempo de espera agotado")
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Error desconocido")
    }
}