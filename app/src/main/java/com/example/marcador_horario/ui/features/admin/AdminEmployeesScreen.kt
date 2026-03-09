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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ── Modelos ───────────────────────────────────────────────────────────────────
enum class EmployeeStatus { WORKING, ON_BREAK, ABSENT }

data class Employee(
    val id: Int,
    val name: String,
    val department: String,
    val role: String,
    val status: EmployeeStatus,
    val clockIn: String = "--:--",
    val location: String = "Office"
)

private data class StatusFilterConfig(
    val label: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun AdminEmployeesScreen(navController: NavController, isDarkMode: Boolean) {

    // ── Colores unificados ────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF0D0D0D) else Color(0xFFF0F4FF)
    val cardColor    = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val textColor    = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
    val subTextColor = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
    val dividerColor = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8)
    val fieldBg      = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val headerGradient = if (isDarkMode)
        listOf(Color(0xFF1A1A6E), Color(0xFF0D47A1), Color(0xFF0D0D0D))
    else
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFFF0F4FF))

    // ── Datos ─────────────────────────────────────────────────────────────────
    val allEmployees = remember {
        listOf(
            Employee(1,  "Juan Pérez",     "IT",             "Developer",       EmployeeStatus.WORKING,   "08:00 AM", "Office"),
            Employee(2,  "María García",   "Human Resources","HR Manager",      EmployeeStatus.WORKING,   "08:15 AM", "Home"),
            Employee(3,  "Luis Martínez",  "Finance",        "Accountant",      EmployeeStatus.ON_BREAK,  "08:30 AM", "Office"),
            Employee(4,  "Ana Fernández",  "Marketing",      "Designer",        EmployeeStatus.WORKING,   "09:00 AM", "Home"),
            Employee(5,  "Carlos Ruiz",    "IT",             "DevOps",          EmployeeStatus.WORKING,   "08:45 AM", "Office"),
            Employee(6,  "Sara Méndez",    "Sales",          "Sales Rep",       EmployeeStatus.ABSENT),
            Employee(7,  "David López",    "Operations",     "Ops Manager",     EmployeeStatus.ON_BREAK,  "07:50 AM", "Office"),
            Employee(8,  "Laura Torres",   "Marketing",      "Content Manager", EmployeeStatus.WORKING,   "09:10 AM", "Home"),
            Employee(9,  "Pedro Sánchez",  "Finance",        "CFO",             EmployeeStatus.ABSENT),
            Employee(10, "Elena Ramírez",  "Human Resources","Recruiter",       EmployeeStatus.WORKING,   "08:05 AM", "Office"),
            Employee(11, "Javier Moreno",  "IT",             "QA Engineer",     EmployeeStatus.WORKING,   "08:20 AM", "Office"),
            Employee(12, "Isabel Jiménez", "Sales",          "Account Manager", EmployeeStatus.ON_BREAK,  "09:00 AM", "Home"),
        )
    }

    // ── Estados de filtro ─────────────────────────────────────────────────────
    var searchQuery      by remember { mutableStateOf("") }
    var statusFilter     by remember { mutableStateOf("All") }
    var departmentFilter by remember { mutableStateOf("All") }

    val departments = listOf("All") + allEmployees.map { it.department }.distinct().sorted()

    val statusConfigs = listOf(
        StatusFilterConfig("All",      Color(0xFF42A5F5), Icons.Filled.Group),
        StatusFilterConfig("Working",  Color(0xFF26A69A), Icons.Filled.CheckCircle),
        StatusFilterConfig("On Break", Color(0xFFFFA726), Icons.Filled.FreeBreakfast),
        StatusFilterConfig("Absent",   Color(0xFFEF5350), Icons.Filled.PersonOff)
    )

    val filteredEmployees = remember(searchQuery, statusFilter, departmentFilter) {
        allEmployees.filter { emp ->
            val matchSearch = searchQuery.isBlank() ||
                    emp.name.contains(searchQuery, ignoreCase = true) ||
                    emp.department.contains(searchQuery, ignoreCase = true) ||
                    emp.role.contains(searchQuery, ignoreCase = true)
            val matchStatus = when (statusFilter) {
                "Working"  -> emp.status == EmployeeStatus.WORKING
                "On Break" -> emp.status == EmployeeStatus.ON_BREAK
                "Absent"   -> emp.status == EmployeeStatus.ABSENT
                else       -> true
            }
            val matchDept = departmentFilter == "All" || emp.department == departmentFilter
            matchSearch && matchStatus && matchDept
        }
    }

    val workingCount  = allEmployees.count { it.status == EmployeeStatus.WORKING }
    val breakCount    = allEmployees.count { it.status == EmployeeStatus.ON_BREAK }
    val absentCount   = allEmployees.count { it.status == EmployeeStatus.ABSENT }

    // ── Animación de entrada ──────────────────────────────────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); cardsVisible = true }

    val panelSlide by animateFloatAsState(
        targetValue   = if (cardsVisible) 0f else 70f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "panelSlide"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {

        // ── CAPA 1: HEADER ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .background(brush = Brush.verticalGradient(colors = headerGradient))
        ) {
            // Círculos decorativos
            Box(
                modifier = Modifier
                    .size(220.dp).offset(x = (-60).dp, y = (-50).dp)
                    .background(
                        Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(150.dp).align(Alignment.TopEnd).offset(x = 50.dp, y = 10.dp)
                    .background(
                        Brush.radialGradient(listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)),
                        CircleShape
                    )
            )

            // Botón back
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 85.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Employees",
                    color         = Color.White,
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    "Staff management",
                    color    = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // KPI strip animado
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderKpi("${allEmployees.size}", "Total")
                    HeaderKpi("$workingCount",        "Working")
                    HeaderKpi("$breakCount",          "Break")
                    HeaderKpi("$absentCount",         "Absent")
                }
            }
        }

        // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.64f)
                .align(Alignment.BottomCenter)
                .offset(y = panelSlide.dp),
            shape           = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
            color           = bgColor,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {

                // ── BUSCADOR ──────────────────────────────────────────────────
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                ) {
                    TextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        placeholder   = {
                            Text(
                                "Search by name, role, department…",
                                color    = subTextColor,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon   = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                        },
                        trailingIcon  = {
                            AnimatedVisibility(
                                visible = searchQuery.isNotEmpty(),
                                enter   = fadeIn() + scaleIn(),
                                exit    = fadeOut() + scaleOut()
                            ) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = null, tint = subTextColor, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine      = true,
                        shape           = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        colors          = TextFieldDefaults.colors(
                            focusedContainerColor   = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor        = textColor,
                            unfocusedTextColor      = textColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── FILTROS DE ESTADO ─────────────────────────────────────────
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(statusConfigs.size) { i ->
                        val cfg        = statusConfigs[i]
                        val isSelected = statusFilter == cfg.label
                        val count = when (cfg.label) {
                            "Working"  -> workingCount
                            "On Break" -> breakCount
                            "Absent"   -> absentCount
                            else       -> allEmployees.size
                        }
                        val bgAnim by animateColorAsState(
                            targetValue   = if (isSelected) cfg.color else cardColor,
                            animationSpec = tween(250), label = "sBg_${cfg.label}"
                        )
                        val txtAnim by animateColorAsState(
                            targetValue   = if (isSelected) Color.White else subTextColor,
                            animationSpec = tween(250), label = "sTxt_${cfg.label}"
                        )
                        Surface(
                            modifier        = Modifier.clip(RoundedCornerShape(50.dp)).clickable { statusFilter = cfg.label },
                            shape           = RoundedCornerShape(50.dp),
                            color           = bgAnim,
                            border          = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                            shadowElevation = if (isSelected) 6.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(cfg.icon, contentDescription = null, tint = txtAnim, modifier = Modifier.size(14.dp))
                                Text(cfg.label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium, color = txtAnim)
                                Surface(shape = CircleShape, color = if (isSelected) Color.White.copy(alpha = 0.25f) else cfg.color.copy(alpha = 0.12f)) {
                                    Text("$count", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else cfg.color,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── FILTROS DE DEPARTAMENTO ───────────────────────────────────
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(departments.size) { i ->
                        val dept       = departments[i]
                        val isSelected = departmentFilter == dept
                        val bgAnim by animateColorAsState(
                            targetValue   = if (isSelected) Color(0xFF1565C0) else cardColor,
                            animationSpec = tween(250), label = "dBg_$dept"
                        )
                        val txtAnim by animateColorAsState(
                            targetValue   = if (isSelected) Color.White else subTextColor,
                            animationSpec = tween(250), label = "dTxt_$dept"
                        )
                        Surface(
                            modifier        = Modifier.clip(RoundedCornerShape(50.dp)).clickable { departmentFilter = dept },
                            shape           = RoundedCornerShape(50.dp),
                            color           = bgAnim,
                            border          = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Text(
                                dept,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = txtAnim,
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Contador de resultados ────────────────────────────────────
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty() || statusFilter != "All" || departmentFilter != "All",
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${filteredEmployees.size} result${if (filteredEmployees.size != 1) "s" else ""}",
                            fontSize   = 12.sp,
                            color      = subTextColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (searchQuery.isNotEmpty() || statusFilter != "All" || departmentFilter != "All") {
                            Text(
                                "Clear all",
                                fontSize   = 12.sp,
                                color      = Color(0xFF42A5F5),
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.clickable {
                                    searchQuery      = ""
                                    statusFilter     = "All"
                                    departmentFilter = "All"
                                }
                            )
                        }
                    }
                }

                // ── LISTA DE EMPLEADOS ────────────────────────────────────────
                AnimatedContent(
                    targetState = filteredEmployees.isEmpty(),
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
                                Text("🔍", fontSize = 52.sp)
                                Text(
                                    "No employees found",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 17.sp,
                                    color      = textColor
                                )
                                Text(
                                    "Try a different search\nor filter combination.",
                                    fontSize   = 13.sp,
                                    color      = subTextColor,
                                    textAlign  = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedButton(
                                    onClick = { searchQuery = ""; statusFilter = "All"; departmentFilter = "All" },
                                    shape   = RoundedCornerShape(50.dp),
                                    border  = BorderStroke(1.dp, Color(0xFF42A5F5))
                                ) {
                                    Text("Clear filters", color = Color(0xFF42A5F5), fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier            = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(filteredEmployees) { index, employee ->
                                var itemVisible by remember { mutableStateOf(false) }
                                LaunchedEffect(employee.id) {
                                    delay(index * 55L)
                                    itemVisible = true
                                }
                                AnimatedVisibility(
                                    visible = itemVisible,
                                    enter   = slideInVertically(tween(350)) { it / 3 } + fadeIn(tween(350))
                                ) {
                                    EmployeeCard(
                                        employee     = employee,
                                        cardColor    = cardColor,
                                        textColor    = textColor,
                                        subTextColor = subTextColor,
                                        dividerColor = dividerColor
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

// ── KPI del header ────────────────────────────────────────────────────────────
@Composable
private fun HeaderKpi(value: String, label: String) {
    Surface(
        shape  = RoundedCornerShape(14.dp),
        color  = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
        }
    }
}

// ── Tarjeta de empleado mejorada ──────────────────────────────────────────────
@Composable
fun EmployeeCard(
    employee: Employee,
    cardColor: Color,
    textColor: Color,
    subTextColor: Color,
    dividerColor: Color
) {
    val avatarColors = listOf(
        Color(0xFF1565C0), Color(0xFF7B1FA2), Color(0xFF2E7D32),
        Color(0xFFE65100), Color(0xFFC62828), Color(0xFF00838F),
        Color(0xFF4527A0), Color(0xFF283593), Color(0xFF558B2F),
        Color(0xFF6D4C41), Color(0xFF0277BD), Color(0xFFAD1457)
    )
    val avatarColor = avatarColors[(employee.id - 1) % avatarColors.size]
    val initials    = employee.name.split(" ").take(2).joinToString("") { it.first().uppercase() }

    val statusColor = when (employee.status) {
        EmployeeStatus.WORKING  -> Color(0xFF26A69A)
        EmployeeStatus.ON_BREAK -> Color(0xFFFFA726)
        EmployeeStatus.ABSENT   -> Color(0xFFEF5350)
    }
    val statusLabel = when (employee.status) {
        EmployeeStatus.WORKING  -> "Working"
        EmployeeStatus.ON_BREAK -> "On Break"
        EmployeeStatus.ABSENT   -> "Absent"
    }
    val statusIcon = when (employee.status) {
        EmployeeStatus.WORKING  -> Icons.Filled.CheckCircle
        EmployeeStatus.ON_BREAK -> Icons.Filled.FreeBreakfast
        EmployeeStatus.ABSENT   -> Icons.Filled.PersonOff
    }

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { pressed = false },
        label = "empScale"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().scale(scale).clickable { pressed = true },
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar con indicador de estado
                Box(modifier = Modifier.size(52.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(avatarColor.copy(alpha = 0.8f), avatarColor)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    // Indicador de estado en esquina
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(statusColor, CircleShape)
                            .padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(employee.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = textColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(employee.role, fontSize = 12.sp, color = subTextColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    // Departamento con ícono
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Business, contentDescription = null, tint = subTextColor, modifier = Modifier.size(11.dp))
                        Text(employee.department, fontSize = 11.sp, color = subTextColor)
                    }
                }

                // Chip de estado con ícono
                Surface(
                    shape  = RoundedCornerShape(10.dp),
                    color  = statusColor.copy(alpha = 0.1f),
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

            // Fila inferior: clock-in y ubicación
            if (employee.status != EmployeeStatus.ABSENT) {
                HorizontalDivider(color = dividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = subTextColor, modifier = Modifier.size(13.dp))
                        Text("Clocked in ${employee.clockIn}", fontSize = 11.sp, color = subTextColor)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            if (employee.location == "Office") Icons.Filled.LocationOn else Icons.Filled.Home,
                            contentDescription = null,
                            tint     = subTextColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(employee.location, fontSize = 11.sp, color = subTextColor)
                    }
                }
            }
        }
    }
}