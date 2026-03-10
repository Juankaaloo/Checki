package com.example.marcador_horario

import android.app.Application

class MarcadorHorarioApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}