package com.example.marcador_horario.ui.features.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ─── Modelos ──────────────────────────────────────────────────────────────────
enum class EmployeeStatus { WORKING, ON_BREAK, ABSENT }

data class Employee(
    val id: Int,
    val name: String,
    val department: String,
    val role: String,
    val status: EmployeeStatus,
    val clockIn: String = "--:--"
)

@Composable
fun AdminEmployeesScreen(navController: NavController, isDarkMode: Boolean) {

    // ─── Paleta ───────────────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor    = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor    = if (isDarkMode) Color.White       else Color.Black
    val subtextColor = if (isDarkMode) Color(0xFFA0AEC0)  else Color(0xFF6B7280)
    val dividerColor = if (isDarkMode) Color(0xFF333333)  else Color(0xFFE5E7EB)
    val fieldBg      = if (isDarkMode) Color(0xFF2A2A2A)  else Color(0xFFF5F5F5)

    // ─── Datos de ejemplo ─────────────────────────────────────────────────────
    val allEmployees = remember {
        listOf(
            Employee(1,  "Juan Pérez",       "IT",                "Developer",        EmployeeStatus.WORKING,   "08:00 AM"),
            Employee(2,  "María García",     "Human Resources",   "HR Manager",       EmployeeStatus.WORKING,   "08:15 AM"),
            Employee(3,  "Luis Martínez",    "Finance",           "Accountant",       EmployeeStatus.ON_BREAK,  "08:30 AM"),
            Employee(4,  "Ana Fernández",    "Marketing",         "Designer",         EmployeeStatus.WORKING,   "09:00 AM"),
            Employee(5,  "Carlos Ruiz",      "IT",                "DevOps",           EmployeeStatus.WORKING,   "08:45 AM"),
            Employee(6,  "Sara Méndez",      "Sales",             "Sales Rep",        EmployeeStatus.ABSENT),
            Employee(7,  "David López",      "Operations",        "Ops Manager",      EmployeeStatus.ON_BREAK,  "07:50 AM"),
            Employee(8,  "Laura Torres",     "Marketing",         "Content Manager",  EmployeeStatus.WORKING,   "09:10 AM"),
            Employee(9,  "Pedro Sánchez",    "Finance",           "CFO",              EmployeeStatus.ABSENT),
            Employee(10, "Elena Ramírez",    "Human Resources",   "Recruiter",        EmployeeStatus.WORKING,   "08:05 AM"),
            Employee(11, "Javier Moreno",    "IT",                "QA Engineer",      EmployeeStatus.WORKING,   "08:20 AM"),
            Employee(12, "Isabel Jiménez",   "Sales",             "Account Manager",  EmployeeStatus.ON_BREAK,  "09:00 AM"),
        )
    }

    // ─── Búsqueda ─────────────────────────────────────────────────────────────
    var searchQuery by remember { mutableStateOf("") }

    val filteredEmployees = remember(searchQuery) {
        if (searchQuery.isBlank()) allEmployees
        else allEmployees.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.department.contains(searchQuery, ignoreCase = true) ||
                    it.role.contains(searchQuery, ignoreCase = true)
        }
    }

    val workingCount  = allEmployees.count { it.status == EmployeeStatus.WORKING }
    val breakCount    = allEmployees.count { it.status == EmployeeStatus.ON_BREAK }
    val absentCount   = allEmployees.count { it.status == EmployeeStatus.ABSENT }

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
                Text("Employees", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Staff management",
                    color    = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // ── Mini resumen de contadores ────────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MiniStatChip("${allEmployees.size} Total",  Color.White.copy(alpha = 0.20f), Color.White)
                    MiniStatChip("$workingCount Working",       Color(0xFF1BD176).copy(alpha = 0.25f), Color.White)
                    MiniStatChip("$absentCount Absent",         Color(0xFFFF5252).copy(alpha = 0.25f), Color.White)
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

                // ── BUSCADOR ──────────────────────────────────────────────────
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter   = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                ) {
                    OutlinedTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        placeholder   = { Text("Search by name, department…", color = subtextColor, fontSize = 14.sp) },
                        leadingIcon   = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = subtextColor)
                        },
                        trailingIcon  = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = subtextColor)
                                }
                            }
                        },
                        singleLine    = true,
                        shape         = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedBorderColor      = Color(0xFF0052D4),
                            unfocusedBorderColor    = Color.Transparent,
                            focusedTextColor        = textColor,
                            unfocusedTextColor      = textColor
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Resultado de búsqueda ─────────────────────────────────────
                AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                    Text(
                        "${filteredEmployees.size} result${if (filteredEmployees.size != 1) "s" else ""} found",
                        fontSize = 13.sp,
                        color    = subtextColor,
                        modifier = Modifier.padding(horizontal = 25.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── LISTA DE EMPLEADOS ────────────────────────────────────────
                if (filteredEmployees.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("🔍", fontSize = 48.sp)
                            Text(
                                "No employees found",
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 17.sp,
                                color      = textColor
                            )
                            Text(
                                "Try a different name or department.",
                                fontSize = 13.sp,
                                color    = subtextColor
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier            = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 25.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(filteredEmployees) { index, employee ->
                            AnimatedVisibility(
                                visible = cardsVisible,
                                enter   = slideInVertically(
                                    animationSpec = tween(400, delayMillis = index * 60, easing = EaseOutCubic),
                                    initialOffsetY = { it / 3 }
                                ) + fadeIn(tween(400, delayMillis = index * 60))
                            ) {
                                EmployeeCard(
                                    employee     = employee,
                                    cardColor    = cardColor,
                                    textColor    = textColor,
                                    subtextColor = subtextColor
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

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Tarjeta de empleado
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EmployeeCard(
    employee: Employee,
    cardColor: Color,
    textColor: Color,
    subtextColor: Color
) {
    // Color único del avatar basado en el id del empleado
    val avatarColors = listOf(
        Color(0xFF0052D4), Color(0xFF8E44AD), Color(0xFF00853B),
        Color(0xFFE65100), Color(0xFFC62828), Color(0xFF00838F),
        Color(0xFF4527A0), Color(0xFF283593), Color(0xFF558B2F),
        Color(0xFF6D4C41), Color(0xFF0277BD), Color(0xFFAD1457)
    )
    val avatarColor = avatarColors[(employee.id - 1) % avatarColors.size]
    val initials    = employee.name.split(" ")
        .take(2)
        .joinToString("") { it.first().uppercase() }

    // Chip de estado
    val (chipBg, chipText, chipLabel) = when (employee.status) {
        EmployeeStatus.WORKING  -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "● Working")
        EmployeeStatus.ON_BREAK -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "● On Break")
        EmployeeStatus.ABSENT   -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "● Absent")
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = cardColor),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Avatar con iniciales ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    initials,
                    color      = Color.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // ── Datos del empleado ────────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    employee.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = textColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    "${employee.role}  ·  ${employee.department}",
                    fontSize = 13.sp,
                    color    = subtextColor
                )
                if (employee.status != EmployeeStatus.ABSENT) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        "Clocked in: ${employee.clockIn}",
                        fontSize = 12.sp,
                        color    = subtextColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // ── Chip de estado ────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = chipBg
            ) {
                Text(
                    chipLabel,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = chipText,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: Mini chip de estadística para la cabecera
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MiniStatChip(label: String, bgColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = bgColor
    ) {
        Text(
            label,
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color      = textColor,
            modifier   = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}