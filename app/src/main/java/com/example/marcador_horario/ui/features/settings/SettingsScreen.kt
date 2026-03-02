package com.example.marcador_horario.ui.features.settings

import androidx.compose.foundation.background
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

@Composable
fun SettingsScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var gpsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("home/Luis") } // Vuelve al Home
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("record") } // Va al Historial
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFD9D9D9))
        ) {
            // CABECERA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0052D4))
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Configuration", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(15.dp)) {
                // TARJETA USUARIO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFE0E0FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF0052D4))
                        }
                        Spacer(modifier = Modifier.width(15.dp))
                        Column {
                            Text("María García Pérez", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Web Designer  ID: 12345", color = Color.Black, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // BLOQUE AJUSTES
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        SettingSwitchItem(Icons.Filled.Notifications, "Notifications", notificationsEnabled) { notificationsEnabled = it }
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp))
                        SettingClickableItem(Icons.Filled.Info, "Language")
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp))
                        // Usamos el icono Star (Estrella) que sí es Básico
                        SettingSwitchItem(Icons.Filled.Star, "Dark Mode", darkModeEnabled) { darkModeEnabled = it }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // GPS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    SettingSwitchItem(Icons.Filled.LocationOn, "GPS", gpsEnabled) { gpsEnabled = it }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // REPORTES Y SALIR
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        // Usamos AutoMirrored.Filled.List que sí es Básico
                        SettingClickableItem(Icons.AutoMirrored.Filled.List, "Reports", hasArrow = true)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFC60000))
                            Spacer(modifier = Modifier.width(15.dp))
                            Text("Log Out", color = Color(0xFFC60000), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingSwitchItem(icon: ImageVector, title: String, state: Boolean, onStateChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = state,
            onCheckedChange = onStateChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF00A313))
        )
    }
}

@Composable
fun SettingClickableItem(icon: ImageVector, title: String, hasArrow: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        if (hasArrow) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        else Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
    }
}