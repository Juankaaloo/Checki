package com.example.marcador_horario.ui.features.record

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RecordScreen(
    navController: NavController,
    isDarkMode: Boolean,
    viewModel: RecordViewModel = viewModel()
) {
    val bgColor      = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor    = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor    = if (isDarkMode) Color.White      else Color.Black
    val iconColor    = if (isDarkMode) Color.LightGray  else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray   else Color.LightGray
    val subtextColor = if (isDarkMode) Color(0xFF9E9E9E) else Color(0xFF757575)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = cardColor) {
                NavigationBarItem(
                    icon     = { Icon(Icons.Filled.Home,      contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick  = { navController.navigate("home") }
                )
                NavigationBarItem(
                    icon     = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick  = { }
                )
                NavigationBarItem(
                    icon     = { Icon(Icons.Filled.Settings,  contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick  = { navController.navigate("settings") }
                )
            }
        }
    ) { paddingValues ->
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
                        .padding(bottom = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "RECORD",
                        color      = Color.White,
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── SELECTOR DE MES CON FLECHAS ───────────────────────────
                    Surface(
                        modifier = Modifier.width(260.dp),
                        shape    = RoundedCornerShape(12.dp),
                        color    = Color.White.copy(alpha = 0.15f),
                        border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Flecha izquierda (mes anterior)
                            IconButton(
                                onClick  = { viewModel.previousMonth() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous month",
                                    tint   = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Nombre del mes animado
                            AnimatedContent(
                                targetState = viewModel.monthLabel,
                                transitionSpec = {
                                    slideInHorizontally(tween(250)) { it / 2 } + fadeIn(tween(250)) togetherWith
                                            slideOutHorizontally(tween(250)) { -it / 2 } + fadeOut(tween(250))
                                },
                                label = "monthLabel"
                            ) { label ->
                                Text(
                                    label,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 15.sp,
                                    color      = Color.White,
                                    textAlign  = TextAlign.Center,
                                    modifier   = Modifier.width(140.dp)
                                )
                            }

                            // Flecha derecha (mes siguiente — deshabilitada si es el mes actual)
                            IconButton(
                                onClick  = { viewModel.nextMonth() },
                                enabled  = !viewModel.isCurrentMonth,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next month",
                                    tint   = if (viewModel.isCurrentMonth)
                                        Color.White.copy(alpha = 0.3f)
                                    else
                                        Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .align(Alignment.BottomCenter),
                shape           = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                color           = bgColor,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp)
                        .padding(top = 32.dp, bottom = 10.dp)
                ) {

                    // ── RESUMEN MENSUAL ───────────────────────────────────────
                    AnimatedContent(
                        targetState = viewModel.monthLabel,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "summary"
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(containerColor = cardColor),
                            shape    = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Días trabajados
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${viewModel.monthlyDays}",
                                        fontSize   = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color(0xFF0052D4)
                                    )
                                    Text(
                                        "Days worked",
                                        fontSize = 12.sp,
                                        color    = subtextColor
                                    )
                                }

                                // Divisor vertical
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(44.dp)
                                        .background(dividerColor)
                                )

                                // Total horas
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        viewModel.monthlySummary,
                                        fontSize   = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color(0xFF0052D4)
                                    )
                                    Text(
                                        "Total hours",
                                        fontSize = 12.sp,
                                        color    = subtextColor
                                    )
                                }

                                // Divisor vertical
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(44.dp)
                                        .background(dividerColor)
                                )

                                // Promedio diario
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val avgMins = if (viewModel.monthlyDays > 0)
                                        viewModel.currentRecords.sumOf { it.workedMinutes } / viewModel.monthlyDays
                                    else 0
                                    val avgH = avgMins / 60
                                    val avgM = avgMins % 60
                                    Text(
                                        "%02dh %02dm".format(avgH, avgM),
                                        fontSize   = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color(0xFF0052D4)
                                    )
                                    Text(
                                        "Daily avg",
                                        fontSize = 12.sp,
                                        color    = subtextColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── LISTA DE REGISTROS O PANTALLA VACÍA ──────────────────
                    AnimatedContent(
                        targetState = viewModel.currentRecords,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "recordList"
                    ) { records ->
                        if (records.isEmpty()) {
                            // ── ESTADO VACÍO ──────────────────────────────────
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        "📭",
                                        fontSize = 52.sp
                                    )
                                    Text(
                                        "No records for this month",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 17.sp,
                                        color      = textColor
                                    )
                                    Text(
                                        "Your clock-ins will appear here\nonce you start working.",
                                        fontSize  = 14.sp,
                                        color     = subtextColor,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        } else {
                            // ── LISTA DE TARJETAS ─────────────────────────────
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                                items(records) { record ->
                                    RecordCard(
                                        record       = record,
                                        cardColor    = cardColor,
                                        textColor    = textColor,
                                        dividerColor = dividerColor,
                                        subtextColor = subtextColor,
                                        barColor     = Color(viewModel.barColorForMinutes(record.workedMinutes))
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(10.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de registro individual
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RecordCard(
    record: WorkRecord,
    cardColor: Color,
    textColor: Color,
    dividerColor: Color,
    subtextColor: Color,
    barColor: Color
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            // ── BARRA LATERAL DE COLOR ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(barColor)
            )

            Column(modifier = Modifier.padding(15.dp).fillMaxWidth()) {

                // ── CABECERA: fecha + chip de ubicación ───────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        record.dateLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = textColor
                    )

                    // Chip de ubicación
                    val chipBg   = if (record.location == "Office") Color(0xFF0052D4).copy(alpha = 0.12f)
                    else Color(0xFF1BD176).copy(alpha = 0.12f)
                    val chipText = if (record.location == "Office") Color(0xFF0052D4)
                    else Color(0xFF00853B)
                    val chipIcon = if (record.location == "Office") "🏢" else "🏠"

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = chipBg
                    ) {
                        Text(
                            "$chipIcon ${record.location}",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = chipText,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier  = Modifier.padding(vertical = 10.dp),
                    thickness = 1.dp,
                    color     = dividerColor
                )

                // ── FILA DE DATOS: entrada / salida / tiempo ──────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeDataItem(label = "Entrance", value = record.entrance, textColor = textColor, subtextColor = subtextColor)
                    TimeDataItem(label = "Exit",     value = record.exit,     textColor = textColor, subtextColor = subtextColor)
                    TimeDataItem(label = "Elapsed",  value = record.timeElapsed, textColor = textColor, subtextColor = subtextColor)
                }
            }
        }
    }
}

@Composable
private fun TimeDataItem(label: String, value: String, textColor: Color, subtextColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = subtextColor)
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}