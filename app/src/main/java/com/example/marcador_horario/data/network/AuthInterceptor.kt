package com.example.marcador_horario.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    // Rutas que NO necesitan token
    private val publicPaths = listOf("/api/auth/login", "/api/auth/register", "/api/health")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Si es ruta pública, no añadir token
        val isPublic = publicPaths.any { path.contains(it) }
        if (isPublic) return chain.proceed(request)

        val token = tokenProvider.getToken()
        val authenticatedRequest = if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(authenticatedRequest)
    }
}

interface TokenProvider {
    fun getToken(): String?
}