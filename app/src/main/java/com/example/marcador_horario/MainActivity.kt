package com.example.marcador_horario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.marcador_horario.navigation.NavGraph
import com.example.marcador_horario.ui.theme.Marcador_HorarioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Marcador_HorarioTheme {
                NavGraph()
            }
        }
    }
}