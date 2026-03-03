package com.example.marcador_horario.ui.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Pantalla de Configuración (Settings).
 * Permite al usuario gestionar sus preferencias locales (Tema, Notificaciones, GPS)
 * y proporciona la vía segura para cerrar sesión.
 *
 * @param navController Controlador de navegación para redirigir entre secciones o ejecutar el Logout.
 * @param username Nombre del usuario activo, inyectado desde el estado global.
 * @param isDarkMode Estado actual del tema de la aplicación.
 * @param onThemeChange Función lambda (Callback) que avisa al NavGraph que el usuario quiere cambiar el tema.
 */
@Composable
fun SettingsScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // --- ESTADOS LOCALES ---
    var notificationsEnabled by remember { mutableStateOf(true) }
    var gpsEnabled by remember { mutableStateOf(true) }

    // --- PALETA DE COLORES REACTIVA ---
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFD9D9D9)
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconColor = if (isDarkMode) Color.LightGray else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray

    // Scaffold estructura la vista e incrusta la barra de navegación inferior
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = cardColor) {
                // Navegación a Home
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("home") }
                )
                // Navegación a Record (Historial)
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("record") }
                )
                // Pestaña Actual (Settings) - Destacada visualmente
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(bgColor)) {

            // --- CABECERA ---
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0052D4)).padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Configuration", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(15.dp)) {

                // --- TARJETA DE PERFIL DE USUARIO ---
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Avatar dinámico con fondo adaptativo
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(if(isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE0E0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF0052D4))
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Column {
                            // Inyección dinámica del nombre de usuario (State Hoisting)
                            Text(username, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                            Text("Employee Profile", color = if (isDarkMode) Color.LightGray else Color.Black, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // --- TARJETA DE PREFERENCIAS GENERALES ---
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    Column {
                        SettingSwitchItem(Icons.Filled.Notifications, "Notifications", notificationsEnabled, textColor) { notificationsEnabled = it }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)

                        SettingClickableItem(Icons.Filled.Info, "Language", textColor)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)

                        // Interruptor de Tema. Ejecuta el callback 'onThemeChange' modificando el estado global en NavGraph
                        SettingSwitchItem(Icons.Filled.Star, "Dark Mode", isDarkMode, textColor) { onThemeChange(it) }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // --- TARJETA DE PERMISOS (GPS) ---
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    SettingSwitchItem(Icons.Filled.LocationOn, "GPS", gpsEnabled, textColor) { gpsEnabled = it }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // --- TARJETA DE CUENTA Y SALIDA ---
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    Column {
                        SettingClickableItem(Icons.AutoMirrored.Filled.List, "Reports", textColor, hasArrow = true)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)

                        // Botón de Logout interactivo y seguro
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Seguridad: Al cerrar sesión, navegamos a "login" y limpiamos toda la pila de
                                    // pantallas anteriores (Backstack) para que el usuario no pueda "volver atrás"
                                    // pulsando el botón físico del móvil y ver datos sensibles.
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252))
                            Spacer(modifier = Modifier.width(15.dp))
                            Text("Log Out", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==============================================================================
// MICRO-COMPONENTES (Sub-UI)
// ==============================================================================

/**
 * Componente de fila interactiva que contiene un interruptor (Switch).
 * Utilizado para opciones booleanas (Activar/Desactivar).
 */
@Composable
fun SettingSwitchItem(icon: ImageVector, title: String, state: Boolean, textColor: Color, onStateChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = textColor)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = state,
            onCheckedChange = onStateChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF00A313))
        )
    }
}

/**
 * Componente de fila interactiva genérica para navegación o ejecución de acciones.
 * Muestra un icono a la derecha dependiendo de la propiedad 'hasArrow'.
 */
@Composable
fun SettingClickableItem(icon: ImageVector, title: String, textColor: Color, hasArrow: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = textColor)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.weight(1f)) // Empuja el icono final hacia la derecha

        // Indicador de acción (Flecha o Lista)
        if (hasArrow) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textColor)
        else Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = textColor)
    }
}