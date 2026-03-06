package com.example.marcador_horario.ui.features.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.marcador_horario.R
import com.example.marcador_horario.ui.features.home.HomeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ─── Modelo para la actividad reciente ───────────────────────────────────────
data class ActivityEntry(
    val name: String,
    val action: String,   // "Entrada" | "Salida" | "Descanso"
    val time: String,
    val isEntry: Boolean
)

@Composable
fun AdminScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean,
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(username) { viewModel.userName = username }

    // ─── Paleta ───────────────────────────────────────────────────────────────
    val bgColor       = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor     = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textPrimary   = if (isDarkMode) Color.White       else Color(0xFF1F2937)
    val textSecondary = if (isDarkMode) Color(0xFFA0AEC0) else Color(0xFF6B7280)
    val dividerColor  = if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E7EB)

    // ─── Datos de estadísticas ────────────────────────────────────────────────
    val totalEmployees  = 42
    val activeEmployees = 38
    val absentEmployees = 4
    val pendingReports  = 3

    // ─── Estado del sistema ───────────────────────────────────────────────────
    var systemOnline by remember { mutableStateOf(true) }

    // ─── Actividad reciente con horas reales ─────────────────────────────────
    val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val now = Date()
    val activityList = remember {
        listOf(
            ActivityEntry("Luis Martínez",  "Entrada",  fmt.format(Date(now.time - 3_600_000)), true),
            ActivityEntry("María García",   "Entrada",  fmt.format(Date(now.time - 3_300_000)), true),
            ActivityEntry("David López",    "Salida",   fmt.format(Date(now.time - 2_700_000)), false),
            ActivityEntry("Ana Fernández",  "Descanso", fmt.format(Date(now.time - 1_800_000)), false),
            ActivityEntry("Carlos Ruiz",    "Entrada",  fmt.format(Date(now.time - 900_000)),   true),
            ActivityEntry("Sara Méndez",    "Salida",   fmt.format(Date(now.time - 600_000)),   false),
        )
    }

    // ─── Animación de entrada (slide + fade) para tarjetas ────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        cardsVisible = true
    }

    Scaffold(containerColor = bgColor) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {

            // ── CAPA 1: FONDO AZUL ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkiii),
                        contentDescription = "App Logo",
                        modifier = Modifier.width(140.dp).height(55.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Welcome, ${viewModel.userName}",
                        color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        viewModel.currentDate.replaceFirstChar { it.uppercase() },
                        color = Color.White, fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            viewModel.currentTime,
                            color = Color.White, fontSize = 60.sp, fontWeight = FontWeight.Medium
                        )
                        Text(
                            " ${viewModel.currentAmPm}",
                            color = Color.White, fontSize = 22.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }

            // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                color = bgColor,
                shadowElevation = 8.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp)
                        .padding(top = 35.dp, bottom = 10.dp)
                ) {

                    // ── 1. TARJETA DE ESTADÍSTICAS ANIMADAS ───────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardColor),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AnimatedStatItem(totalEmployees,  "Total",    textPrimary,        textSecondary)
                                        VerticalDivider(modifier = Modifier.height(40.dp), color = dividerColor)
                                        AnimatedStatItem(activeEmployees, "Active",   Color(0xFF1BD176),  textSecondary)
                                        VerticalDivider(modifier = Modifier.height(40.dp), color = dividerColor)
                                        AnimatedStatItem(absentEmployees, "Absent",   Color(0xFFFF5252),  textSecondary)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // ── BARRA DE PROGRESO DE ASISTENCIA ───────
                                    val attendanceProgress = activeEmployees.toFloat() / totalEmployees.toFloat()
                                    val animatedProgress by animateFloatAsState(
                                        targetValue = if (cardsVisible) attendanceProgress else 0f,
                                        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
                                        label = "attendanceProgress"
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Today's attendance",
                                            fontSize = 13.sp,
                                            color = textSecondary
                                        )
                                        Text(
                                            "$activeEmployees / $totalEmployees",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier.fillMaxWidth().height(10.dp),
                                        color = when {
                                            attendanceProgress >= 0.9f -> Color(0xFF1BD176)
                                            attendanceProgress >= 0.7f -> Color(0xFFF2994A)
                                            else                       -> Color(0xFFFF5252)
                                        },
                                        trackColor = dividerColor,
                                        strokeCap = StrokeCap.Round
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "${(attendanceProgress * 100).toInt()}% of staff clocked in",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(25.dp)) }

                    // ── 2. CHIP ESTADO DEL SISTEMA ────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 80) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Quick Management",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = textPrimary
                                )
                                // Chip Online / Offline
                                val chipColor = if (systemOnline) Color(0xFF1BD176) else Color(0xFFFF5252)
                                val chipBg    = if (isDarkMode)
                                    chipColor.copy(alpha = 0.2f)
                                else
                                    chipColor.copy(alpha = 0.12f)
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = chipBg,
                                    modifier = Modifier.clickable { systemOnline = !systemOnline }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(chipColor)
                                        )
                                        Text(
                                            if (systemOnline) "Online" else "Offline",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = chipColor
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    // ── 3. ACCIONES RÁPIDAS CON BADGE ─────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 160) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                ActionCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Employees",
                                    icon = Icons.Filled.AccountCircle,
                                    color = Color(0xFF0052D4),
                                    cardColor = cardColor,
                                    textColor = textPrimary
                                ) { navController.navigate("admin_employees") }

                                // Reportes con badge de notificaciones
                                BadgedActionCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Reports",
                                    icon = Icons.AutoMirrored.Filled.List,
                                    color = Color(0xFFF2994A),
                                    cardColor = cardColor,
                                    textColor = textPrimary,
                                    badgeCount = pendingReports
                                ) { navController.navigate("admin_reports") }
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 220) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                ActionCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Settings",
                                    icon = Icons.Filled.Settings,
                                    color = Color(0xFF8E44AD),
                                    cardColor = cardColor,
                                    textColor = textPrimary
                                ) { navController.navigate("admin_settings") }

                                ActionCard(
                                    modifier = Modifier.weight(1f),
                                    title = "Log Out",
                                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                                    color = Color(0xFFFF5252),
                                    cardColor = cardColor,
                                    textColor = textPrimary
                                ) { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                    // ── 4. ACTIVIDAD RECIENTE ─────────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 300) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Recent Activity",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = textPrimary
                                )
                                Text(
                                    "See all",
                                    color = Color(0xFF0052D4),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 360) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardColor),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    activityList.forEachIndexed { index, entry ->
                                        ActivityRow(
                                            entry = entry,
                                            textColor = textPrimary,
                                            subTextColor = textSecondary
                                        )
                                        if (index < activityList.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 15.dp),
                                                color = dividerColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Estadística con contador animado
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnimatedStatItem(
    target: Int,
    label: String,
    valueColor: Color,
    labelColor: Color
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        started = true
    }
    val animatedValue by animateIntAsState(
        targetValue = if (started) target else 0,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "stat_$label"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "$animatedValue",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = valueColor
        )
        Text(label, color = labelColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de acción normal
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    cardColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de acción con badge de notificaciones
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun BadgedActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    cardColor: Color,
    textColor: Color,
    badgeCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono con badge
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(
                            containerColor = Color(0xFFFF5252),
                            contentColor   = Color.White
                        ) {
                            Text(
                                "$badgeCount",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Fila de actividad reciente
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ActivityRow(
    entry: ActivityEntry,
    textColor: Color,
    subTextColor: Color
) {
    val statusColor = when (entry.action) {
        "Entrada"  -> Color(0xFF1BD176)
        "Salida"   -> Color(0xFFF2994A)
        else       -> Color(0xFF0052D4) // Descanso
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(statusColor)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            Text(
                "${entry.action} • ${entry.time}",
                color = subTextColor,
                fontSize = 14.sp
            )
        }
        Icon(Icons.Filled.MoreVert, contentDescription = null, tint = subTextColor)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Wrapper de animación de entrada (slide up + fade)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun AnimatedCard(
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 400, delayMillis = delayMs, easing = EaseOutCubic),
            initialOffsetY = { it / 3 }
        ) + fadeIn(
            animationSpec = tween(durationMillis = 400, delayMillis = delayMs)
        )
    ) {
        content()
    }
}