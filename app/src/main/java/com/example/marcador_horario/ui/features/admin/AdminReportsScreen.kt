package com.example.marcador_horario.ui.features.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ─── Modelos ──────────────────────────────────────────────────────────────────
enum class ReportStatus { PENDING, APPROVED, REJECTED }

data class Report(
    val id: Int,
    val employeeName: String,
    val reason: String,
    val message: String,
    val date: String,
    var status: ReportStatus = ReportStatus.PENDING
)

@Composable
fun AdminReportsScreen(navController: NavController, isDarkMode: Boolean) {

    // ─── Paleta ───────────────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor    = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor    = if (isDarkMode) Color.White      else Color.Black
    val subtextColor = if (isDarkMode) Color(0xFFA0AEC0) else Color(0xFF6B7280)
    val dividerColor = if (isDarkMode) Color(0xFF333333) else Color(0xFFE5E7EB)

    // ─── Datos de ejemplo ─────────────────────────────────────────────────────
    val reports = remember {
        mutableStateListOf(
            Report(1, "Luis Martínez",   "Medical appointment", "Llegaré 2 horas tarde hoy por el dentista.",                       "Today, 07:45 AM"),
            Report(2, "María García",    "Cannot clock in",     "El sistema no me deja registrar la entrada desde esta mañana.",     "Today, 08:10 AM"),
            Report(3, "David López",     "System error",        "La app se cierra sola al intentar marcar la salida.",               "Today, 09:00 AM"),
            Report(4, "Ana Fernández",   "Medical appointment", "Tengo revisión médica esta tarde, saldré a las 15:00.",             "Yesterday, 16:30 PM"),
            Report(5, "Carlos Ruiz",     "Other",               "Necesito ajustar mi horario esta semana por temas familiares.",     "Yesterday, 14:00 PM"),
            Report(6, "Sara Méndez",     "Cannot clock in",     "No puedo fichar desde hace dos días, necesito ayuda urgente.",      "2 days ago, 08:00 AM"),
        )
    }

    // ─── Filtro activo ────────────────────────────────────────────────────────
    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Approved", "Rejected")

    val filteredReports = when (activeFilter) {
        "Pending"  -> reports.filter { it.status == ReportStatus.PENDING }
        "Approved" -> reports.filter { it.status == ReportStatus.APPROVED }
        "Rejected" -> reports.filter { it.status == ReportStatus.REJECTED }
        else       -> reports.toList()
    }

    val pendingCount = reports.count { it.status == ReportStatus.PENDING }

    // ─── Animación de entrada ─────────────────────────────────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); cardsVisible = true }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {

        // ── CAPA 1: FONDO AZUL ────────────────────────────────────────────────
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
            IconButton(
                onClick  = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 40.dp, start = 15.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Reports", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // ── Contador de pendientes animado ────────────────────────────
                AnimatedContent(
                    targetState = pendingCount,
                    transitionSpec = {
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    },
                    label = "pendingCounter"
                ) { count ->
                    if (count > 0) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.White.copy(alpha = 0.20f)
                        ) {
                            Text(
                                "$count pending report${if (count != 1) "s" else ""}",
                                color    = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFF1BD176).copy(alpha = 0.25f)
                        ) {
                            Text(
                                "✓ All reports resolved",
                                color    = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────────
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
                    .padding(top = 30.dp)
            ) {
                // ── FILTROS ───────────────────────────────────────────────────
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { filter ->
                            val isSelected = activeFilter == filter
                            val count = when (filter) {
                                "Pending"  -> reports.count { it.status == ReportStatus.PENDING }
                                "Approved" -> reports.count { it.status == ReportStatus.APPROVED }
                                "Rejected" -> reports.count { it.status == ReportStatus.REJECTED }
                                else       -> reports.size
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick  = { activeFilter = filter },
                                label = {
                                    Text(
                                        "$filter ($count)",
                                        fontSize   = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0052D4),
                                    selectedLabelColor     = Color.White,
                                    containerColor         = if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF0F0F0),
                                    labelColor             = if (isDarkMode) Color.LightGray else Color.DarkGray
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled          = true,
                                    selected         = isSelected,
                                    borderColor      = Color.Transparent,
                                    selectedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── LISTA DE REPORTES ─────────────────────────────────────────
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    // Estado vacío
                    if (filteredReports.isEmpty()) {
                        item {
                            AnimatedVisibility(
                                visible = cardsVisible,
                                enter   = fadeIn(tween(400))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("📭", fontSize = 48.sp)
                                        Text(
                                            "No reports here",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize   = 17.sp,
                                            color      = textColor
                                        )
                                        Text(
                                            "Nothing to show for this filter.",
                                            fontSize  = 13.sp,
                                            color     = subtextColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    itemsIndexed(filteredReports) { index, report ->
                        AnimatedVisibility(
                            visible = cardsVisible,
                            enter   = slideInVertically(
                                animationSpec = tween(400, delayMillis = index * 70, easing = EaseOutCubic),
                                initialOffsetY = { it / 3 }
                            ) + fadeIn(tween(400, delayMillis = index * 70))
                        ) {
                            ReportCard(
                                report       = report,
                                cardColor    = cardColor,
                                textColor    = textColor,
                                subtextColor = subtextColor,
                                dividerColor = dividerColor,
                                onApprove    = {
                                    val i = reports.indexOfFirst { it.id == report.id }
                                    if (i != -1) reports[i] = reports[i].copy(status = ReportStatus.APPROVED)
                                },
                                onReject     = {
                                    val i = reports.indexOfFirst { it.id == report.id }
                                    if (i != -1) reports[i] = reports[i].copy(status = ReportStatus.REJECTED)
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de reporte individual
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ReportCard(
    report: Report,
    cardColor: Color,
    textColor: Color,
    subtextColor: Color,
    dividerColor: Color,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    // Color de la barra lateral según motivo
    val barColor = when (report.reason) {
        "Medical appointment" -> Color(0xFF0052D4)
        "Cannot clock in"     -> Color(0xFFFF5252)
        "System error"        -> Color(0xFFF2994A)
        else                  -> Color(0xFF8E44AD)
    }

    // Colores e icono del chip de estado
    val (chipBg, chipText, chipLabel) = when (report.status) {
        ReportStatus.PENDING  -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100),  "● Pending")
        ReportStatus.APPROVED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32),  "✓ Approved")
        ReportStatus.REJECTED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828),  "✕ Rejected")
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            // Barra lateral de color
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(barColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // ── Cabecera: nombre + chip ───────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            report.employeeName,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                            color      = textColor
                        )
                        Text(
                            report.date,
                            fontSize = 12.sp,
                            color    = subtextColor
                        )
                    }

                    // Chip de estado animado
                    AnimatedContent(
                        targetState = report.status,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label = "statusChip_${report.id}"
                    ) { status ->
                        val (bg, fg, label) = when (status) {
                            ReportStatus.PENDING  -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "● Pending")
                            ReportStatus.APPROVED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "✓ Approved")
                            ReportStatus.REJECTED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "✕ Rejected")
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = bg
                        ) {
                            Text(
                                label,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = fg,
                                modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Motivo ────────────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = barColor.copy(alpha = 0.10f)
                ) {
                    Text(
                        report.reason,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = barColor,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Mensaje ───────────────────────────────────────────────────
                Text(
                    report.message,
                    fontSize   = 14.sp,
                    color      = textColor,
                    lineHeight = 20.sp
                )

                // ── Botones Aprobar / Rechazar (solo si está pendiente) ───────
                AnimatedVisibility(
                    visible = report.status == ReportStatus.PENDING,
                    enter   = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit    = fadeOut(tween(200)) + shrinkVertically(tween(200))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = dividerColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Botón Aprobar
                            Button(
                                onClick  = onApprove,
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1BD176)
                                )
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint     = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            // Botón Rechazar
                            OutlinedButton(
                                onClick  = onReject,
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFFF5252)
                                ),
                                border   = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252))
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint     = Color(0xFFFF5252)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reject", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}