package com.example.marcador_horario.ui.features.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.marcador_horario.data.model.ReportDto
import kotlinx.coroutines.delay

// ── Configuración de filtros ──────────────────────────────────────────────────
private data class FilterConfig(
    val label: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun AdminReportsScreen(
    navController: NavController,
    isDarkMode: Boolean,
    viewModel: AdminReportsViewModel
) {

    // ── Colores unificados ────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF0D0D0D) else Color(0xFFF0F4FF)
    val cardColor    = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val textColor    = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
    val subTextColor = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
    val dividerColor = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8)
    val headerGradient = if (isDarkMode)
        listOf(Color(0xFF1A1A6E), Color(0xFF0D47A1), Color(0xFF0D0D0D))
    else
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFFF0F4FF))

    // ── Datos del ViewModel ─────────────────────────────────────────────────
    val allReports      = viewModel.allReports
    val filteredReports = viewModel.filteredReports
    val activeFilter    = viewModel.activeFilter
    val pendingCount    = viewModel.pendingCount
    val approvedCount   = viewModel.approvedCount
    val rejectedCount   = viewModel.rejectedCount

    val filterConfigs = listOf(
        FilterConfig("All",      Color(0xFF42A5F5), Icons.Filled.List),
        FilterConfig("Pending",  Color(0xFFFFA726), Icons.Filled.HourglassEmpty),
        FilterConfig("Approved", Color(0xFF26A69A), Icons.Filled.CheckCircle),
        FilterConfig("Rejected", Color(0xFFEF5350), Icons.Filled.Cancel)
    )

    // ── Animación de entrada ──────────────────────────────────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); cardsVisible = true }

    val headerSlide by animateFloatAsState(
        targetValue   = if (cardsVisible) 0f else -30f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label         = "headerSlide"
    )
    val panelSlide by animateFloatAsState(
        targetValue   = if (cardsVisible) 0f else 60f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "panelSlide"
    )

    // ── Snackbar ──────────────────────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            viewModel.errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data, containerColor = Color(0xFFEF5350),
                    contentColor = Color.White, shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {

            // ── CAPA 1: HEADER ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .background(brush = Brush.verticalGradient(colors = headerGradient))
                    .offset(y = headerSlide.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp).offset(x = (-60).dp, y = (-50).dp)
                        .background(
                            brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(150.dp).align(Alignment.TopEnd).offset(x = 50.dp, y = 10.dp)
                        .background(
                            brush = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)),
                            shape = CircleShape
                        )
                )

                IconButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 42.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(bottom = 85.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Reports", color = Color.White, fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp
                    )
                    Text(
                        "Manage employee requests", color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    if (!viewModel.isLoading) {
                        // Badge de pendientes animado
                        AnimatedContent(
                            targetState = pendingCount,
                            transitionSpec = {
                                (slideInVertically { -it } + fadeIn()) togetherWith
                                        (slideOutVertically { it } + fadeOut())
                            },
                            label = "pendingBadge"
                        ) { count ->
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = if (count > 0) Color(0xFFFFA726).copy(alpha = 0.25f)
                                else Color(0xFF26A69A).copy(alpha = 0.25f),
                                border = BorderStroke(
                                    1.dp,
                                    if (count > 0) Color(0xFFFFA726).copy(alpha = 0.5f)
                                    else Color(0xFF26A69A).copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        if (count > 0) Icons.Filled.HourglassEmpty else Icons.Filled.CheckCircle,
                                        contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        if (count > 0) "$count pending report${if (count != 1) "s" else ""}"
                                        else "All reports resolved",
                                        color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MiniKpi("Pending", pendingCount, Color(0xFFFFA726))
                            MiniKpi("Approved", approvedCount, Color(0xFF26A69A))
                            MiniKpi("Rejected", rejectedCount, Color(0xFFEF5350))
                        }
                    }
                }
            }

            // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.63f)
                    .align(Alignment.BottomCenter)
                    .offset(y = panelSlide.dp),
                shape           = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color           = bgColor,
                shadowElevation = 20.dp
            ) {
                if (viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF42A5F5))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = 24.dp)
                    ) {

                        // ── FILTROS ───────────────────────────────────────────
                        AnimatedVisibility(
                            visible = cardsVisible,
                            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                        ) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filterConfigs.size) { index ->
                                    val config     = filterConfigs[index]
                                    val isSelected = activeFilter == config.label
                                    val count = when (config.label) {
                                        "Pending"  -> pendingCount
                                        "Approved" -> approvedCount
                                        "Rejected" -> rejectedCount
                                        else       -> allReports.size
                                    }
                                    val bgAnim by animateColorAsState(
                                        targetValue = if (isSelected) config.color else cardColor,
                                        animationSpec = tween(250), label = "filterBg_${config.label}"
                                    )
                                    val textAnim by animateColorAsState(
                                        targetValue = if (isSelected) Color.White else subTextColor,
                                        animationSpec = tween(250), label = "filterTxt_${config.label}"
                                    )
                                    Surface(
                                        modifier = Modifier.clip(RoundedCornerShape(50.dp))
                                            .clickable { viewModel.updateFilter(config.label) },
                                        shape = RoundedCornerShape(50.dp), color = bgAnim,
                                        border = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                                        shadowElevation = if (isSelected) 6.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(config.icon, contentDescription = null, tint = textAnim, modifier = Modifier.size(14.dp))
                                            Text(config.label, fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium, color = textAnim)
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) Color.White.copy(alpha = 0.25f)
                                                else config.color.copy(alpha = 0.12f)
                                            ) {
                                                Text("$count", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else config.color,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── LISTA DE REPORTES ─────────────────────────────────
                        AnimatedContent(
                            targetState = filteredReports.isEmpty(),
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                            label = "listOrEmpty"
                        ) { isEmpty ->
                            if (isEmpty) {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text("📭", fontSize = 52.sp)
                                        Text("No reports here", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textColor)
                                        Text("Nothing to show for\nthis filter.", fontSize = 13.sp, color = subTextColor, textAlign = TextAlign.Center, lineHeight = 20.sp)
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    itemsIndexed(filteredReports) { index, report ->
                                        var itemVisible by remember { mutableStateOf(false) }
                                        LaunchedEffect(report.id) {
                                            delay(index * 60L)
                                            itemVisible = true
                                        }
                                        AnimatedVisibility(
                                            visible = itemVisible,
                                            enter = slideInVertically(tween(350)) { it / 3 } + fadeIn(tween(350))
                                        ) {
                                            ReportCard(
                                                report       = report,
                                                cardColor    = cardColor,
                                                textColor    = textColor,
                                                subtextColor = subTextColor,
                                                dividerColor = dividerColor,
                                                isDarkMode   = isDarkMode,
                                                onApprove    = { viewModel.approveReport(report.id) },
                                                onReject     = { viewModel.rejectReport(report.id) }
                                            )
                                        }
                                    }
                                    item { Spacer(modifier = Modifier.height(16.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Mini KPI del header ───────────────────────────────────────────────────────
@Composable
private fun MiniKpi(label: String, count: Int, color: Color) {
    Surface(
        shape  = RoundedCornerShape(12.dp),
        color  = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
        }
    }
}

// ── Tarjeta de reporte (ahora usa ReportDto) ──────────────────────────────────
@Composable
fun ReportCard(
    report: ReportDto,
    cardColor: Color,
    textColor: Color,
    subtextColor: Color,
    dividerColor: Color,
    isDarkMode: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val reasonConfig = when (report.type) {
        "medical"      -> Pair(Color(0xFF42A5F5), Icons.Filled.LocalHospital)
        "clock_issue"  -> Pair(Color(0xFFEF5350), Icons.Filled.AccessTime)
        "system_error" -> Pair(Color(0xFFFFA726), Icons.Filled.BugReport)
        "vacation"     -> Pair(Color(0xFF26A69A), Icons.Filled.BeachAccess)
        "incident"     -> Pair(Color(0xFFAB47BC), Icons.Filled.Warning)
        else           -> Pair(Color(0xFF78909C), Icons.Filled.MoreHoriz)
    }
    val barColor = reasonConfig.first
    val barIcon  = reasonConfig.second

    val statusColor = when (report.status) {
        "pending"  -> Color(0xFFFFA726)
        "approved" -> Color(0xFF26A69A)
        "rejected" -> Color(0xFFEF5350)
        else       -> Color(0xFF78909C)
    }
    val statusLabel = report.status.replaceFirstChar { it.uppercase() }
    val statusIcon = when (report.status) {
        "pending"  -> Icons.Filled.HourglassEmpty
        "approved" -> Icons.Filled.CheckCircle
        "rejected" -> Icons.Filled.Cancel
        else       -> Icons.Filled.Info
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.3f)))
                    )
            )

            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                // ── Cabecera ──────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(barColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                (report.employeeName ?: "?").first().toString(),
                                fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = barColor
                            )
                        }
                        Column {
                            Text(
                                report.employeeName ?: "Unknown",
                                fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = textColor
                            )
                            Text(report.date, fontSize = 11.sp, color = subtextColor)
                        }
                    }

                    // Chip de estado
                    AnimatedContent(
                        targetState = report.status,
                        transitionSpec = {
                            (fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.85f)) togetherWith
                                    (fadeOut(tween(200)) + scaleOut(tween(200)))
                        },
                        label = "statusChip_${report.id}"
                    ) { _ ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = statusColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                                Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Chip de tipo ──────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = barColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, barColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(barIcon, contentDescription = null, tint = barColor, modifier = Modifier.size(13.dp))
                        Text(
                            report.type.replace("_", " ").replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = barColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Descripción ───────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkMode) Color.White.copy(alpha = 0.04f)
                    else Color(0xFF1A1A2E).copy(alpha = 0.03f)
                ) {
                    Text(
                        report.description ?: report.reason ?: "",
                        fontSize = 13.sp, color = textColor.copy(alpha = 0.85f),
                        lineHeight = 20.sp, modifier = Modifier.padding(10.dp)
                    )
                }

                // ── Botones de acción ─────────────────────────────────────
                AnimatedVisibility(
                    visible = report.status == "pending",
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(250)) + shrinkVertically(tween(250))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = dividerColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onApprove,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A)),
                                elevation = ButtonDefaults.buttonElevation(4.dp)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = onReject,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, Color(0xFFEF5350)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350))
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF5350))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reject", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF5350))
                            }
                        }
                    }
                }
            }
        }
    }
}