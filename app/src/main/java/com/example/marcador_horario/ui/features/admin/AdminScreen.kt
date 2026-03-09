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
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.marcador_horario.R
import com.example.marcador_horario.ui.features.home.HomeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class ActivityEntry(
    val name: String,
    val action: String,
    val time: String,
    val isEntry: Boolean,
    val location: String = "Office"
)

@Composable
fun AdminScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean,
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(username) { viewModel.userName = username }

    // ── Colores unificados ────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF0D0D0D) else Color(0xFFF0F4FF)
    val cardColor    = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val textPrimary  = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
    val textSecondary= if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
    val dividerColor = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8)
    val headerGradient = if (isDarkMode)
        listOf(Color(0xFF1A1A6E), Color(0xFF0D47A1), Color(0xFF0D0D0D))
    else
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFFF0F4FF))

    // ── Datos ─────────────────────────────────────────────────────────────────
    val totalEmployees  = 42
    val activeEmployees = 38
    val absentEmployees = 4
    val onBreakEmployees = 5
    val pendingReports  = 3
    val attendanceRate  = activeEmployees.toFloat() / totalEmployees.toFloat()

    var systemOnline by remember { mutableStateOf(true) }

    val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val now = Date()
    val activityList = remember {
        listOf(
            ActivityEntry("Luis Martínez",  "Clock In",    fmt.format(Date(now.time - 3_600_000)), true,  "Office"),
            ActivityEntry("María García",   "Clock In",    fmt.format(Date(now.time - 3_300_000)), true,  "Home"),
            ActivityEntry("David López",    "Clock Out",   fmt.format(Date(now.time - 2_700_000)), false, "Office"),
            ActivityEntry("Ana Fernández",  "Break",       fmt.format(Date(now.time - 1_800_000)), false, "Office"),
            ActivityEntry("Carlos Ruiz",    "Clock In",    fmt.format(Date(now.time - 900_000)),   true,  "Home"),
            ActivityEntry("Sara Méndez",    "Clock Out",   fmt.format(Date(now.time - 600_000)),   false, "Office"),
        )
    }

    // ── Animación de entrada ──────────────────────────────────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); cardsVisible = true }

    // ── Pulsación del indicador Online ────────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    Scaffold(containerColor = bgColor) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgColor)
        ) {

            // ── CAPA 1: HEADER ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .background(brush = Brush.verticalGradient(colors = headerGradient))
            ) {
                // Círculos decorativos
                Box(
                    modifier = Modifier.size(220.dp).offset(x = (-60).dp, y = (-50).dp)
                        .background(
                            brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 50.dp, y = 10.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)),
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier.fillMaxSize().padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkiii),
                        contentDescription = "Logo",
                        modifier = Modifier.width(140.dp).height(52.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Admin Panel",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Welcome, ${viewModel.userName}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        viewModel.currentDate.replaceFirstChar { it.uppercase() },
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            viewModel.currentTime,
                            color = Color.White,
                            fontSize = 58.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            " ${viewModel.currentAmPm}",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }

            // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.66f)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color = bgColor,
                shadowElevation = 20.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(top = 26.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {

                    // ── 1. KPI CARDS — fila superior ─────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                KpiCard(
                                    modifier    = Modifier.weight(1f),
                                    value       = "$totalEmployees",
                                    label       = "Total",
                                    icon        = Icons.Filled.Group,
                                    color       = Color(0xFF42A5F5),
                                    cardColor   = cardColor,
                                    textColor   = textPrimary,
                                    subColor    = textSecondary
                                )
                                KpiCard(
                                    modifier    = Modifier.weight(1f),
                                    value       = "$activeEmployees",
                                    label       = "Active",
                                    icon        = Icons.Filled.CheckCircle,
                                    color       = Color(0xFF26A69A),
                                    cardColor   = cardColor,
                                    textColor   = textPrimary,
                                    subColor    = textSecondary
                                )
                                KpiCard(
                                    modifier    = Modifier.weight(1f),
                                    value       = "$absentEmployees",
                                    label       = "Absent",
                                    icon        = Icons.Filled.PersonOff,
                                    color       = Color(0xFFEF5350),
                                    cardColor   = cardColor,
                                    textColor   = textPrimary,
                                    subColor    = textSecondary
                                )
                                KpiCard(
                                    modifier    = Modifier.weight(1f),
                                    value       = "$onBreakEmployees",
                                    label       = "Break",
                                    icon        = Icons.Filled.FreeBreakfast,
                                    color       = Color(0xFFFFA726),
                                    cardColor   = cardColor,
                                    textColor   = textPrimary,
                                    subColor    = textSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // ── 2. BARRA DE ASISTENCIA ────────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 80) {
                            Card(
                                modifier  = Modifier.fillMaxWidth(),
                                colors    = CardDefaults.cardColors(containerColor = cardColor),
                                shape     = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "Attendance Rate",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize   = 13.sp,
                                                color      = textSecondary,
                                                letterSpacing = 0.8.sp
                                            )
                                            Text(
                                                "${(attendanceRate * 100).toInt()}%",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize   = 28.sp,
                                                color      = when {
                                                    attendanceRate >= 0.9f -> Color(0xFF26A69A)
                                                    attendanceRate >= 0.7f -> Color(0xFFFFA726)
                                                    else                   -> Color(0xFFEF5350)
                                                }
                                            )
                                        }
                                        // Chip sistema online/offline
                                        val chipColor = if (systemOnline) Color(0xFF26A69A) else Color(0xFFEF5350)
                                        Surface(
                                            shape    = RoundedCornerShape(50.dp),
                                            color    = chipColor.copy(alpha = 0.12f),
                                            modifier = Modifier.clickable { systemOnline = !systemOnline }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .scale(if (systemOnline) pulseScale else 1f)
                                                            .background(chipColor.copy(alpha = 0.3f), CircleShape)
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .background(chipColor, CircleShape)
                                                    )
                                                }
                                                Text(
                                                    if (systemOnline) "Online" else "Offline",
                                                    fontSize   = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color      = chipColor
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    val animatedProgress by animateFloatAsState(
                                        targetValue    = if (cardsVisible) attendanceRate else 0f,
                                        animationSpec  = tween(1200, easing = EaseOutCubic),
                                        label          = "attendanceBar"
                                    )
                                    LinearProgressIndicator(
                                        progress   = { animatedProgress },
                                        modifier   = Modifier.fillMaxWidth().height(10.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color      = when {
                                            attendanceRate >= 0.9f -> Color(0xFF26A69A)
                                            attendanceRate >= 0.7f -> Color(0xFFFFA726)
                                            else                   -> Color(0xFFEF5350)
                                        },
                                        trackColor = dividerColor,
                                        strokeCap  = StrokeCap.Round
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "$activeEmployees of $totalEmployees staff clocked in",
                                            fontSize = 12.sp,
                                            color    = textSecondary
                                        )
                                        Text(
                                            "Today",
                                            fontSize   = 12.sp,
                                            color      = textSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // ── 3. ACCIONES RÁPIDAS ───────────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 160) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Quick Actions",
                                    fontWeight    = FontWeight.ExtraBold,
                                    fontSize      = 18.sp,
                                    color         = textPrimary,
                                    letterSpacing = 0.2.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color(0xFF42A5F5).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        "4 actions",
                                        fontSize   = 11.sp,
                                        color      = Color(0xFF42A5F5),
                                        fontWeight = FontWeight.SemiBold,
                                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 200) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ActionCard(
                                    modifier  = Modifier.weight(1f),
                                    title     = "Employees",
                                    subtitle  = "Manage staff",
                                    icon      = Icons.Filled.Group,
                                    color     = Color(0xFF42A5F5),
                                    cardColor = cardColor,
                                    textColor = textPrimary,
                                    subColor  = textSecondary
                                ) { navController.navigate("admin_employees") }

                                BadgedActionCard(
                                    modifier    = Modifier.weight(1f),
                                    title       = "Reports",
                                    subtitle    = "Pending items",
                                    icon        = Icons.AutoMirrored.Filled.List,
                                    color       = Color(0xFFFFA726),
                                    cardColor   = cardColor,
                                    textColor   = textPrimary,
                                    subColor    = textSecondary,
                                    badgeCount  = pendingReports
                                ) { navController.navigate("admin_reports") }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 250) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ActionCard(
                                    modifier  = Modifier.weight(1f),
                                    title     = "Settings",
                                    subtitle  = "Preferences",
                                    icon      = Icons.Filled.Settings,
                                    color     = Color(0xFFAB47BC),
                                    cardColor = cardColor,
                                    textColor = textPrimary,
                                    subColor  = textSecondary
                                ) { navController.navigate("admin_settings") }

                                ActionCard(
                                    modifier  = Modifier.weight(1f),
                                    title     = "Log Out",
                                    subtitle  = "Sign out",
                                    icon      = Icons.AutoMirrored.Filled.ExitToApp,
                                    color     = Color(0xFFEF5350),
                                    cardColor = cardColor,
                                    textColor = textPrimary,
                                    subColor  = textSecondary
                                ) { navController.navigate("login") { popUpTo(0) { inclusive = true } } }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // ── 4. WIDGET DE DISTRIBUCIÓN ─────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 300) {
                            Card(
                                modifier  = Modifier.fillMaxWidth(),
                                colors    = CardDefaults.cardColors(containerColor = cardColor),
                                shape     = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        "LOCATION SPLIT",
                                        fontWeight    = FontWeight.Bold,
                                        fontSize      = 11.sp,
                                        color         = textSecondary,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Office
                                        LocationSplitItem(
                                            modifier  = Modifier.weight(1f),
                                            label     = "Office",
                                            count     = 24,
                                            total     = activeEmployees,
                                            color     = Color(0xFF42A5F5),
                                            icon      = Icons.Filled.LocationOn,
                                            visible   = cardsVisible,
                                            textColor = textPrimary,
                                            subColor  = textSecondary
                                        )
                                        // Home
                                        LocationSplitItem(
                                            modifier  = Modifier.weight(1f),
                                            label     = "Remote",
                                            count     = 14,
                                            total     = activeEmployees,
                                            color     = Color(0xFF26A69A),
                                            icon      = Icons.Filled.Home,
                                            visible   = cardsVisible,
                                            textColor = textPrimary,
                                            subColor  = textSecondary
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // ── 5. ACTIVIDAD RECIENTE ─────────────────────────────────
                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 360) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Recent Activity",
                                    fontWeight    = FontWeight.ExtraBold,
                                    fontSize      = 18.sp,
                                    color         = textPrimary,
                                    letterSpacing = 0.2.sp
                                )
                                Text(
                                    "See all →",
                                    color      = Color(0xFF42A5F5),
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier   = Modifier.clickable { }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        AnimatedCard(visible = cardsVisible, delayMs = 400) {
                            Card(
                                modifier  = Modifier.fillMaxWidth(),
                                colors    = CardDefaults.cardColors(containerColor = cardColor),
                                shape     = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    activityList.forEachIndexed { index, entry ->
                                        ActivityRow(
                                            entry        = entry,
                                            textColor    = textPrimary,
                                            subTextColor = textSecondary,
                                            dividerColor = dividerColor,
                                            showDivider  = index < activityList.lastIndex
                                        )
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

// ── KPI Card pequeña ──────────────────────────────────────────────────────────
@Composable
fun KpiCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    cardColor: Color,
    textColor: Color,
    subColor: Color
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(200); started = true }
    val animVal by animateIntAsState(
        targetValue   = if (started) value.toIntOrNull() ?: 0 else 0,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "kpi_$label"
    )

    Card(
        modifier  = modifier,
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(
                "$animVal",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 22.sp,
                color      = color
            )
            Text(
                label,
                fontSize   = 11.sp,
                color      = subColor,
                fontWeight = FontWeight.Medium,
                textAlign  = TextAlign.Center
            )
        }
    }
}

// ── Widget distribución por ubicación ────────────────────────────────────────
@Composable
fun LocationSplitItem(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    total: Int,
    color: Color,
    icon: ImageVector,
    visible: Boolean,
    textColor: Color,
    subColor: Color
) {
    val progress = count.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue   = if (visible) progress else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "locProg_$label"
    )
    Surface(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        color    = color.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = color)
            }
            Text(
                "$count",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 26.sp,
                color      = textColor
            )
            LinearProgressIndicator(
                progress   = { animatedProgress },
                modifier   = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color      = color,
                trackColor = color.copy(alpha = 0.2f),
                strokeCap  = StrokeCap.Round
            )
            Text(
                "${(progress * 100).toInt()}% of active",
                fontSize = 11.sp,
                color    = subColor
            )
        }
    }
}

// ── Tarjeta de acción mejorada ────────────────────────────────────────────────
@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    color: Color,
    cardColor: Color,
    textColor: Color,
    subColor: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { pressed = false },
        label = "actionScale"
    )
    Card(
        modifier  = modifier
            .scale(scale)
            .clickable { pressed = true; onClick() },
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = textColor)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, fontSize = 11.sp, color = subColor)
                }
            }
        }
    }
}

// ── Tarjeta de acción con badge ───────────────────────────────────────────────
@Composable
fun BadgedActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    color: Color,
    cardColor: Color,
    textColor: Color,
    subColor: Color,
    badgeCount: Int,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { pressed = false },
        label = "badgedScale"
    )
    Card(
        modifier  = modifier
            .scale(scale)
            .clickable { pressed = true; onClick() },
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BadgedBox(
                badge = {
                    if (badgeCount > 0) {
                        Badge(containerColor = Color(0xFFEF5350), contentColor = Color.White) {
                            Text("$badgeCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
            }
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = textColor)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, fontSize = 11.sp, color = subColor)
                }
            }
        }
    }
}

// ── Fila de actividad reciente mejorada ───────────────────────────────────────
@Composable
fun ActivityRow(
    entry: ActivityEntry,
    textColor: Color,
    subTextColor: Color,
    dividerColor: Color,
    showDivider: Boolean = true
) {
    val actionColor = when (entry.action) {
        "Clock In"  -> Color(0xFF26A69A)
        "Clock Out" -> Color(0xFFEF5350)
        "Break"     -> Color(0xFFFFA726)
        else        -> Color(0xFF42A5F5)
    }
    val actionIcon = when (entry.action) {
        "Clock In"  -> Icons.AutoMirrored.Filled.Login
        "Clock Out" -> Icons.AutoMirrored.Filled.ExitToApp
        "Break"     -> Icons.Filled.FreeBreakfast
        else        -> Icons.Filled.MoreHoriz
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con inicial
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(actionColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    entry.name.first().toString(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 16.sp,
                    color      = actionColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = textColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(entry.time, color = subTextColor, fontSize = 12.sp)
                    Text("•", color = subTextColor, fontSize = 12.sp)
                    Text(
                        entry.location,
                        color    = subTextColor,
                        fontSize = 12.sp
                    )
                }
            }
            // Badge de acción
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = actionColor.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        actionIcon,
                        contentDescription = null,
                        tint     = actionColor,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        entry.action,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = actionColor
                    )
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier  = Modifier.padding(horizontal = 16.dp),
                color     = dividerColor,
                thickness = 1.dp
            )
        }
    }
}

// ── Wrapper de animación de entrada ──────────────────────────────────────────
@Composable
fun AnimatedCard(
    visible: Boolean,
    delayMs: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter   = slideInVertically(
            animationSpec = tween(400, delayMillis = delayMs, easing = EaseOutCubic),
            initialOffsetY = { it / 3 }
        ) + fadeIn(tween(400, delayMillis = delayMs))
    ) {
        content()
    }
}