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
import com.example.marcador_horario.data.model.UserDto
import kotlinx.coroutines.delay

// ── Configuración de filtros de estado ───────────────────────────────────────
private data class StatusFilterConfig(
    val label: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun AdminEmployeesScreen(
    navController: NavController,
    isDarkMode: Boolean,
    viewModel: AdminEmployeesViewModel
) {

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

    // ── Datos del ViewModel ─────────────────────────────────────────────────
    val allEmployees      = viewModel.allEmployees
    val filteredEmployees = viewModel.filteredEmployees
    val searchQuery       = viewModel.searchQuery
    val statusFilter      = viewModel.statusFilter
    val departmentFilter  = viewModel.departmentFilter
    val departments       = viewModel.departments
    val workingCount      = viewModel.workingCount
    val breakCount        = viewModel.breakCount
    val absentCount       = viewModel.absentCount

    val statusConfigs = listOf(
        StatusFilterConfig("All",      Color(0xFF42A5F5), Icons.Filled.Group),
        StatusFilterConfig("Working",  Color(0xFF26A69A), Icons.Filled.CheckCircle),
        StatusFilterConfig("Absent",   Color(0xFFEF5350), Icons.Filled.PersonOff)
    )

    // ── Animación de entrada ──────────────────────────────────────────────────
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); cardsVisible = true }

    val panelSlide by animateFloatAsState(
        targetValue   = if (cardsVisible) 0f else 70f,
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
            ) {
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
                        "Employees", color = Color.White, fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp
                    )
                    Text(
                        "Staff management", color = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    if (!viewModel.isLoading) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            HeaderKpi("${allEmployees.size}", "Total")
                            HeaderKpi("$workingCount", "Active")
                            HeaderKpi("$absentCount", "Inactive")
                        }
                    }
                }
            }

            // ── CAPA 2: PANEL INFERIOR ────────────────────────────────────────
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
                if (viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF42A5F5))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp)
                    ) {

                        // ── BUSCADOR ──────────────────────────────────────────
                        AnimatedVisibility(
                            visible = cardsVisible,
                            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                        ) {
                            TextField(
                                value         = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                modifier      = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                placeholder   = {
                                    Text("Search by name, role, department…", color = subTextColor, fontSize = 13.sp)
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                                },
                                trailingIcon = {
                                    AnimatedVisibility(
                                        visible = searchQuery.isNotEmpty(),
                                        enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()
                                    ) {
                                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
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

                        // ── FILTROS DE ESTADO ─────────────────────────────────
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(statusConfigs.size) { i ->
                                val cfg        = statusConfigs[i]
                                val isSelected = statusFilter == cfg.label
                                val count = when (cfg.label) {
                                    "Working" -> workingCount
                                    "Absent"  -> absentCount
                                    else      -> allEmployees.size
                                }
                                val bgAnim by animateColorAsState(
                                    targetValue = if (isSelected) cfg.color else cardColor,
                                    animationSpec = tween(250), label = "sBg_${cfg.label}"
                                )
                                val txtAnim by animateColorAsState(
                                    targetValue = if (isSelected) Color.White else subTextColor,
                                    animationSpec = tween(250), label = "sTxt_${cfg.label}"
                                )
                                Surface(
                                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                                        .clickable { viewModel.updateStatusFilter(cfg.label) },
                                    shape = RoundedCornerShape(50.dp), color = bgAnim,
                                    border = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
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
                                            Text("$count", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else cfg.color,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // ── FILTROS DE DEPARTAMENTO ───────────────────────────
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(departments.size) { i ->
                                val dept       = departments[i]
                                val isSelected = departmentFilter == dept
                                val bgAnim by animateColorAsState(
                                    targetValue = if (isSelected) Color(0xFF1565C0) else cardColor,
                                    animationSpec = tween(250), label = "dBg_$dept"
                                )
                                val txtAnim by animateColorAsState(
                                    targetValue = if (isSelected) Color.White else subTextColor,
                                    animationSpec = tween(250), label = "dTxt_$dept"
                                )
                                Surface(
                                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                                        .clickable { viewModel.updateDepartmentFilter(dept) },
                                    shape = RoundedCornerShape(50.dp), color = bgAnim,
                                    border = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                                    shadowElevation = if (isSelected) 4.dp else 0.dp
                                ) {
                                    Text(
                                        dept, fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = txtAnim,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // ── Contador de resultados ────────────────────────────
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty() || statusFilter != "All" || departmentFilter != "All",
                            enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${filteredEmployees.size} result${if (filteredEmployees.size != 1) "s" else ""}",
                                    fontSize = 12.sp, color = subTextColor, fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Clear all", fontSize = 12.sp, color = Color(0xFF42A5F5),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable { viewModel.clearFilters() }
                                )
                            }
                        }

                        // ── LISTA DE EMPLEADOS ────────────────────────────────
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
                                        Text("No employees found", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = textColor)
                                        Text("Try a different search\nor filter combination.", fontSize = 13.sp, color = subTextColor, textAlign = TextAlign.Center, lineHeight = 20.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.clearFilters() },
                                            shape = RoundedCornerShape(50.dp),
                                            border = BorderStroke(1.dp, Color(0xFF42A5F5))
                                        ) {
                                            Text("Clear filters", color = Color(0xFF42A5F5), fontSize = 13.sp)
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
                                            enter = slideInVertically(tween(350)) { it / 3 } + fadeIn(tween(350))
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

// ── Tarjeta de empleado (ahora usa UserDto) ───────────────────────────────────
@Composable
fun EmployeeCard(
    employee: UserDto,
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

    val isActive    = employee.isActive
    val statusColor = if (isActive) Color(0xFF26A69A) else Color(0xFFEF5350)
    val statusLabel = if (isActive) "Active" else "Inactive"
    val statusIcon  = if (isActive) Icons.Filled.CheckCircle else Icons.Filled.PersonOff

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
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(52.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                brush = Brush.radialGradient(listOf(avatarColor.copy(alpha = 0.8f), avatarColor)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    }
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
                    Text(employee.jobTitle ?: "Employee", fontSize = 12.sp, color = subTextColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Business, contentDescription = null, tint = subTextColor, modifier = Modifier.size(11.dp))
                        Text(employee.department ?: "—", fontSize = 11.sp, color = subTextColor)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp), color = statusColor.copy(alpha = 0.1f),
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

            if (isActive) {
                HorizontalDivider(color = dividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = subTextColor, modifier = Modifier.size(13.dp))
                        Text(employee.email, fontSize = 11.sp, color = subTextColor)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            if (employee.location == "office") Icons.Filled.LocationOn else Icons.Filled.Home,
                            contentDescription = null, tint = subTextColor, modifier = Modifier.size(13.dp)
                        )
                        Text(employee.location.replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = subTextColor)
                    }
                }
            }
        }
    }
}