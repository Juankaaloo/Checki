package com.example.marcador_horario.ui.features.record

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

// ── Filtros disponibles ───────────────────────────────────────────────────────
private enum class LocationFilter(val label: String) {
    ALL("All"), OFFICE("Office"), HOME("Home")
}

@Composable
fun RecordScreen(
    navController: NavController,
    isDarkMode: Boolean,
    viewModel: RecordViewModel = viewModel()
) {
    // ── Colores ───────────────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF0D0D0D) else Color(0xFFF0F4FF)
    val cardColor    = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val textColor    = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
    val subTextColor = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
    val dividerColor = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8)
    val headerGradient = if (isDarkMode)
        listOf(Color(0xFF1A1A6E), Color(0xFF0D47A1), Color(0xFF0D0D0D))
    else
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFFF0F4FF))

    // ── Estado local de filtros y búsqueda ────────────────────────────────────
    var searchQuery by remember { mutableStateOf("") }
    var searchExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(LocationFilter.ALL) }

    // ── Filtrado de registros ─────────────────────────────────────────────────
    val filteredRecords = remember(viewModel.currentRecords, searchQuery, selectedFilter) {
        viewModel.currentRecords.filter { record ->
            val matchesLocation = when (selectedFilter) {
                LocationFilter.ALL    -> true
                LocationFilter.OFFICE -> record.location == "Office"
                LocationFilter.HOME   -> record.location == "Home"
            }
            val matchesSearch = searchQuery.isBlank() ||
                    record.dateLabel.contains(searchQuery, ignoreCase = true) ||
                    record.entrance.contains(searchQuery, ignoreCase = true) ||
                    record.exit.contains(searchQuery, ignoreCase = true)
            matchesLocation && matchesSearch
        }
    }

    // ── Animación de entrada ──────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "headerAlpha"
    )
    val cardSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 60f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "cardSlide"
    )

    Scaffold(
        bottomBar = {
            Surface(
                color           = cardColor,
                shadowElevation = 16.dp,
                tonalElevation  = 4.dp
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    modifier       = Modifier.height(65.dp)
                ) {
                    NavigationBarItem(
                        icon     = {
                            Icon(
                                Icons.Filled.Home,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = subTextColor
                            )
                        },
                        selected = false,
                        onClick  = { navController.navigate("home") },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        icon     = {
                            Icon(
                                Icons.Filled.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = Color(0xFF42A5F5)
                            )
                        },
                        selected = true,
                        onClick  = {},
                        colors   = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFF42A5F5).copy(alpha = 0.15f)
                        )
                    )
                    NavigationBarItem(
                        icon     = {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = subTextColor
                            )
                        },
                        selected = false,
                        onClick  = { navController.navigate("settings") },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                    )
                }
            }
        }
    ) { paddingValues ->
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
                    modifier = Modifier
                        .size(200.dp)
                        .offset(x = (-50).dp, y = (-50).dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = 10.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Título
                    Text(
                        "Work Records",
                        color         = Color.White,
                        fontSize      = 28.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Your attendance history",
                        color    = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // ── SELECTOR DE MES ───────────────────────────────────────
                    Surface(
                        modifier = Modifier.width(270.dp),
                        shape    = RoundedCornerShape(14.dp),
                        color    = Color.White.copy(alpha = 0.15f),
                        border   = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick  = { viewModel.previousMonth() },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous month",
                                    tint     = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            AnimatedContent(
                                targetState = viewModel.monthLabel,
                                transitionSpec = {
                                    (slideInHorizontally(tween(250)) { it / 2 } + fadeIn(tween(250))) togetherWith
                                            (slideOutHorizontally(tween(250)) { -it / 2 } + fadeOut(tween(250)))
                                },
                                label = "monthLabel"
                            ) { label ->
                                Text(
                                    label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp,
                                    color      = Color.White,
                                    textAlign  = TextAlign.Center,
                                    modifier   = Modifier.width(150.dp)
                                )
                            }

                            IconButton(
                                onClick  = { viewModel.nextMonth() },
                                enabled  = !viewModel.isCurrentMonth,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next month",
                                    tint     = if (viewModel.isCurrentMonth)
                                        Color.White.copy(alpha = 0.25f) else Color.White,
                                    modifier = Modifier.size(26.dp)
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
                    .fillMaxHeight(0.66f)
                    .align(Alignment.BottomCenter)
                    .offset(y = cardSlide.dp),
                shape           = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color           = bgColor,
                shadowElevation = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .padding(top = 26.dp, bottom = 10.dp)
                ) {

                    // ── RESUMEN MENSUAL ───────────────────────────────────────
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        shape     = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryStatItem(
                                value    = "${viewModel.monthlyDays}",
                                label    = "Days",
                                color    = Color(0xFF42A5F5)
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp).height(40.dp)
                                    .background(dividerColor)
                            )
                            SummaryStatItem(
                                value = viewModel.monthlySummary,
                                label = "Total hrs",
                                color = Color(0xFF26A69A)
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp).height(40.dp)
                                    .background(dividerColor)
                            )
                            val avgMins = if (viewModel.monthlyDays > 0)
                                viewModel.currentRecords.sumOf { it.workedMinutes } / viewModel.monthlyDays
                            else 0
                            SummaryStatItem(
                                value = "%02dh%02dm".format(avgMins / 60, avgMins % 60),
                                label = "Avg/day",
                                color = Color(0xFFFFA726)
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp).height(40.dp)
                                    .background(dividerColor)
                            )
                            SummaryStatItem(
                                value = "${filteredRecords.size}",
                                label = "Filtered",
                                color = Color(0xFFAB47BC)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ── BARRA DE BÚSQUEDA + FILTROS ───────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Barra de búsqueda expandible
                        AnimatedContent(
                            targetState = searchExpanded,
                            transitionSpec = {
                                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                            },
                            label = "searchBar",
                            modifier = Modifier.weight(1f)
                        ) { expanded ->
                            if (expanded) {
                                TextField(
                                    value         = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder   = { Text("Search by date or time…", fontSize = 13.sp) },
                                    modifier      = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape         = RoundedCornerShape(14.dp),
                                    singleLine    = true,
                                    leadingIcon   = {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = null,
                                            tint = Color(0xFF42A5F5),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon  = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(
                                                    Icons.Filled.Close,
                                                    contentDescription = null,
                                                    tint = subTextColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor   = cardColor,
                                        unfocusedContainerColor = cardColor,
                                        focusedIndicatorColor   = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor        = textColor,
                                        unfocusedTextColor      = textColor
                                    )
                                )
                            } else {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .clickable { searchExpanded = true },
                                    shape  = RoundedCornerShape(14.dp),
                                    color  = cardColor,
                                    border = BorderStroke(1.dp, dividerColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = null,
                                            tint = subTextColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            "Search records…",
                                            fontSize = 13.sp,
                                            color    = subTextColor
                                        )
                                    }
                                }
                            }
                        }

                        // Botón cerrar búsqueda
                        AnimatedVisibility(
                            visible = searchExpanded,
                            enter   = fadeIn() + expandHorizontally(),
                            exit    = fadeOut() + shrinkHorizontally()
                        ) {
                            IconButton(
                                onClick = {
                                    searchExpanded = false
                                    searchQuery    = ""
                                },
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(cardColor, RoundedCornerShape(14.dp))
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = subTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── CHIPS DE FILTRO ───────────────────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LocationFilter.entries.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val chipBg by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF1565C0) else cardColor,
                                animationSpec = tween(250),
                                label = "chip_${filter.name}"
                            )
                            val chipText by animateColorAsState(
                                targetValue = if (isSelected) Color.White else subTextColor,
                                animationSpec = tween(250),
                                label = "chipTxt_${filter.name}"
                            )
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .clickable { selectedFilter = filter },
                                shape  = RoundedCornerShape(50.dp),
                                color  = chipBg,
                                border = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                                shadowElevation = if (isSelected) 4.dp else 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (filter != LocationFilter.ALL) {
                                        Icon(
                                            Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint     = chipText,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                    Text(
                                        filter.label,
                                        fontSize   = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color      = chipText
                                    )
                                }
                            }
                        }

                        // Badge con número de resultados
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty() || selectedFilter != LocationFilter.ALL,
                            enter   = fadeIn() + expandHorizontally(),
                            exit    = fadeOut() + shrinkHorizontally()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = Color(0xFF42A5F5).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "${filteredRecords.size} result${if (filteredRecords.size != 1) "s" else ""}",
                                    fontSize   = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color(0xFF42A5F5),
                                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ── LISTA DE REGISTROS ────────────────────────────────────
                    AnimatedContent(
                        targetState = filteredRecords.isEmpty(),
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                        label = "listOrEmpty"
                    ) { isEmpty ->
                        if (isEmpty) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        if (searchQuery.isNotEmpty() || selectedFilter != LocationFilter.ALL)
                                            "🔍" else "📭",
                                        fontSize = 52.sp
                                    )
                                    Text(
                                        if (searchQuery.isNotEmpty() || selectedFilter != LocationFilter.ALL)
                                            "No matches found"
                                        else
                                            "No records this month",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 17.sp,
                                        color      = textColor
                                    )
                                    Text(
                                        if (searchQuery.isNotEmpty() || selectedFilter != LocationFilter.ALL)
                                            "Try a different search or filter."
                                        else
                                            "Your clock-ins will appear here\nonce you start working.",
                                        fontSize   = 13.sp,
                                        color      = subTextColor,
                                        textAlign  = TextAlign.Center,
                                        lineHeight = 20.sp
                                    )
                                    // Botón limpiar filtros
                                    if (searchQuery.isNotEmpty() || selectedFilter != LocationFilter.ALL) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedButton(
                                            onClick = {
                                                searchQuery    = ""
                                                selectedFilter = LocationFilter.ALL
                                            },
                                            shape  = RoundedCornerShape(50.dp),
                                            border = BorderStroke(1.dp, Color(0xFF42A5F5))
                                        ) {
                                            Text(
                                                "Clear filters",
                                                color    = Color(0xFF42A5F5),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                itemsIndexed(filteredRecords) { index, record ->
                                    var itemVisible by remember { mutableStateOf(false) }
                                    LaunchedEffect(record) {
                                        kotlinx.coroutines.delay(index * 60L)
                                        itemVisible = true
                                    }
                                    AnimatedVisibility(
                                        visible = itemVisible,
                                        enter   = fadeIn(tween(300)) + slideInVertically(
                                            tween(300)
                                        ) { it / 3 }
                                    ) {
                                        RecordCard(
                                            record       = record,
                                            cardColor    = cardColor,
                                            textColor    = textColor,
                                            dividerColor = dividerColor,
                                            subtextColor = subTextColor,
                                            barColor     = Color(viewModel.barColorForMinutes(record.workedMinutes))
                                        )
                                    }
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

// ── Stat item del resumen mensual ─────────────────────────────────────────────
@Composable
private fun SummaryStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize   = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = color
        )
        Text(
            label,
            fontSize = 11.sp,
            color    = color.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Tarjeta de registro individual ───────────────────────────────────────────
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
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {

            // Barra lateral con gradiente
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.4f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth()
            ) {
                // Cabecera: fecha + chip ubicación
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            record.dateLabel,
                            fontWeight    = FontWeight.ExtraBold,
                            fontSize      = 15.sp,
                            color         = textColor,
                            letterSpacing = 0.2.sp
                        )
                        Text(
                            record.timeElapsed,
                            fontSize   = 12.sp,
                            color      = barColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Chip de ubicación mejorado
                    val isOffice   = record.location == "Office"
                    val chipBg     = if (isOffice) Color(0xFF1565C0).copy(alpha = 0.1f)
                    else Color(0xFF26A69A).copy(alpha = 0.1f)
                    val chipColor  = if (isOffice) Color(0xFF1565C0) else Color(0xFF26A69A)
                    val chipEmoji  = if (isOffice) "🏢" else "🏠"

                    Surface(
                        shape  = RoundedCornerShape(10.dp),
                        color  = chipBg,
                        border = BorderStroke(1.dp, chipColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            "$chipEmoji ${record.location}",
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color      = chipColor,
                            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier  = Modifier.padding(vertical = 10.dp),
                    thickness = 1.dp,
                    color     = dividerColor
                )

                // Fila de datos con indicadores de color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TimeDataItem(
                        label      = "Entrance",
                        value      = record.entrance,
                        dotColor   = Color(0xFF26A69A),
                        textColor  = textColor,
                        subtextColor = subtextColor
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp).height(36.dp)
                            .align(Alignment.CenterVertically)
                            .background(dividerColor)
                    )
                    TimeDataItem(
                        label      = "Exit",
                        value      = record.exit,
                        dotColor   = Color(0xFFEF5350),
                        textColor  = textColor,
                        subtextColor = subtextColor
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp).height(36.dp)
                            .align(Alignment.CenterVertically)
                            .background(dividerColor)
                    )
                    TimeDataItem(
                        label      = "Elapsed",
                        value      = record.timeElapsed,
                        dotColor   = Color(0xFF42A5F5),
                        textColor  = textColor,
                        subtextColor = subtextColor
                    )
                }
            }
        }
    }
}

// ── Item de dato de tiempo ────────────────────────────────────────────────────
@Composable
private fun TimeDataItem(
    label: String,
    value: String,
    dotColor: Color,
    textColor: Color,
    subtextColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape)
            )
            Text(
                label,
                fontSize = 11.sp,
                color    = subtextColor,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize   = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = textColor
        )
    }
}