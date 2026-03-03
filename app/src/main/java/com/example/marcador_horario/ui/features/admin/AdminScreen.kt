package com.example.marcador_horario.ui.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 * Pantalla del Dashboard de Administración (Exclusiva para perfil Admin).
 * Interfaz de alto nivel que muestra métricas generales, estado de los empleados y accesos rápidos.
 * Utiliza un diseño moderno de superposición (Overlapping UI) y listas perezosas (LazyColumn)
 * para optimizar el rendimiento en pantallas pequeñas.
 *
 * @param navController Gestor de navegación para redirigir (ej. Logout seguro).
 * @param username Nombre del administrador activo, inyectado desde el estado global.
 * @param isDarkMode Estado actual del tema de la app para adaptar la paleta de colores.
 */
@Composable
fun AdminScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean
) {
    // --- PALETA DE COLORES PREMIUM ---
    // Colores calculados dinámicamente. El "Azul Royal" se mantiene constante para dar identidad corporativa,
    // mientras que los fondos y textos mutan dependiendo del tema del sistema (Claro/Oscuro).
    val primaryAdmin = Color(0xFF2563EB) // Azul Royal moderno
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF3F4F6) // Gris súper suave
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF1F2937)
    val textSecondary = if (isDarkMode) Color(0xFFA0AEC0) else Color(0xFF6B7280)
    val dividerColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E7EB)

    // Scaffold provee la estructura base (sin BottomBar en este caso, ya que el admin tiene su propia navegación)
    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        // LazyColumn asegura que todo el Dashboard sea scrolleable y no se rompa (Overflow)
        // en pantallas con resoluciones más pequeñas.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ====================================================================
            // SECCIÓN 1: CABECERA Y MÉTRICAS (Efecto Overlapping)
            // ====================================================================
            item {
                // Box principal que permite apilar elementos uno encima del otro (Z-Index implícito)
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Fondo de la cabecera (Azul)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(
                                color = primaryAdmin,
                                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 25.dp, top = 40.dp, end = 25.dp)
                        ) {
                            Text("Dashboard", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Bienvenido, $username", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                        }
                    }

                    // 2. Tarjeta flotante de Estadísticas
                    // TRUCO UI: Se le aplica un padding-top de 140.dp para forzar que baje y quede "cabalgando"
                    // mitad sobre el fondo azul y mitad sobre el fondo gris.
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 140.dp, start = 20.dp, end = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatItem("42", "Total", textPrimary, textSecondary)
                            VerticalDivider(modifier = Modifier.height(40.dp), color = dividerColor)
                            StatItem("38", "Activos", Color(0xFF1BD176), textSecondary) // Verde = Positivo
                            VerticalDivider(modifier = Modifier.height(40.dp), color = dividerColor)
                            StatItem("4", "Ausentes", Color(0xFFFF5252), textSecondary) // Rojo = Alerta
                        }
                    }
                }
            }

            // ====================================================================
            // SECCIÓN 2: GRID DE ACCIONES RÁPIDAS
            // ====================================================================
            item {
                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Gestión Rápida", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textPrimary)
                    Spacer(modifier = Modifier.height(15.dp))

                    // Filas con Modificadores de Peso (weight = 1f) para crear una cuadrícula perfecta 2x2
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                        ActionCard(Modifier.weight(1f), "Empleados", Icons.Filled.AccountCircle, Color(0xFF0052D4), cardColor, textPrimary) { }
                        ActionCard(Modifier.weight(1f), "Reportes", Icons.AutoMirrored.Filled.List, Color(0xFFF2994A), cardColor, textPrimary) { }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                        ActionCard(Modifier.weight(1f), "Ajustes", Icons.Filled.Settings, Color(0xFF8E44AD), cardColor, textPrimary) { }

                        // Botón de Logout interactivo y seguro (Limpia el Backstack)
                        ActionCard(Modifier.weight(1f), "Salir", Icons.AutoMirrored.Filled.ExitToApp, Color(0xFFFF5252), cardColor, textPrimary) {
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    }
                }
            }

            // ====================================================================
            // SECCIÓN 3: FEED DE ACTIVIDAD RECIENTE
            // ====================================================================
            item {
                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Actividad Reciente", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textPrimary)
                        Text("Ver todo", color = primaryAdmin, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            ActivityRow("Luis Martínez", "Entrada • 08:00 AM", true, textPrimary, textSecondary)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)
                            ActivityRow("María García", "Entrada • 08:15 AM", true, textPrimary, textSecondary)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)
                            ActivityRow("David López", "Salida • 16:30 PM", false, textPrimary, textSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp)) // Margen final inferior
                }
            }
        }
    }
}

// ==============================================================================
// MICROCOMPONENTES (Sub-UI)
// Extraídos del flujo principal para seguir el principio DRY (Don't Repeat Yourself)
// y facilitar la lectura y el mantenimiento del código.
// ==============================================================================

/**
 * Representa una métrica individual dentro de la tarjeta de estadísticas.
 */
@Composable
fun StatItem(value: String, label: String, valueColor: Color, labelColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = valueColor)
        Text(label, color = labelColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Tarjeta clickeable para el grid de acciones rápidas.
 * Crea automáticamente un fondo semitransparente para el icono basándose en su color principal (Alpha = 0.15f).
 */
@Composable
fun ActionCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, color: Color, cardColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(45.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
        }
    }
}

/**
 * Fila de información que representa el fichaje reciente de un empleado.
 * Muestra un indicador visual de estado: Verde para Entradas, Naranja para Salidas.
 */
@Composable
fun ActivityRow(name: String, detail: String, isEntry: Boolean, textColor: Color, subTextColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lógica visual del indicador (Verde = Entrada, Naranja = Salida)
        val statusColor = if (isEntry) Color(0xFF1BD176) else Color(0xFFF2994A)
        Box(
            modifier = Modifier.size(12.dp).clip(CircleShape).background(statusColor)
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            Text(detail, color = subTextColor, fontSize = 14.sp)
        }

        Icon(Icons.Filled.MoreVert, contentDescription = null, tint = subTextColor)
    }
}