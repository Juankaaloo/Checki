package com.example.marcador_horario.ui.features.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.marcador_horario.R

@Composable
fun HomeScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(username) {
        viewModel.userName = username
        viewModel.attachContext(context)
    }

    val bgColor    = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor  = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor  = if (isDarkMode) Color.White      else Color.Black
    val iconColor  = if (isDarkMode) Color.LightGray  else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray

    // ── Animación del botón principal ─────────────────────────────────────────
    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.93f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        finishedListener = { buttonPressed = false },
        label = "buttonScale"
    )

    // ── Color animado del botón principal ─────────────────────────────────────
    val buttonColor by animateColorAsState(
        targetValue = if (viewModel.isPunchedIn) Color(0xFFFF5252) else Color(0xFF1BD176),
        animationSpec = tween(durationMillis = 400),
        label = "buttonColor"
    )

    // ── Color del chip de estado ──────────────────────────────────────────────
    val chipColor = when (viewModel.workStatus) {
        "Working"       -> Color(0xFF1BD176)
        "On Break"      -> Color(0xFFF2994A)
        else            -> Color(0xFF9E9E9E)
    }

    // ── Animación del progreso de horas ───────────────────────────────────────
    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.workProgress,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = cardColor) {
                NavigationBarItem(
                    icon     = { Icon(Icons.Filled.Home,      contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick  = { }
                )
                NavigationBarItem(
                    icon     = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick  = { navController.navigate("record") }
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
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter      = painterResource(id = R.drawable.checkiii),
                        contentDescription = "App Logo",
                        modifier     = Modifier.width(140.dp).height(55.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Welcome, ${viewModel.userName}",
                        color      = Color.White,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        viewModel.currentDate.replaceFirstChar { it.uppercase() },
                        color    = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            viewModel.currentTime,
                            color      = Color.White,
                            fontSize   = 60.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            " ${viewModel.currentAmPm}",
                            color    = Color.White,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }

            // ── CAPA 2: TARJETA INFERIOR ──────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .align(Alignment.BottomCenter),
                shape          = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
                color          = bgColor,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                        .padding(top = 35.dp, bottom = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── CHIP DE ESTADO ────────────────────────────────────────
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = chipColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(bottom = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape  = RoundedCornerShape(50),
                                color  = chipColor,
                                modifier = Modifier.size(9.dp)
                            ) {}
                            Text(
                                text       = viewModel.workStatus,
                                color      = chipColor,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ── 1. TARJETA DE UBICACIÓN ───────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = cardColor),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Job Location:",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp,
                                color      = textColor
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.jobLocation = "Office" },
                                    modifier = Modifier.weight(1f).height(45.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (viewModel.jobLocation == "Office") Color(0xFF0052D4) else Color.Transparent,
                                        contentColor   = if (viewModel.jobLocation == "Office") Color.White else textColor
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (viewModel.jobLocation == "Office") Color.Transparent else dividerColor
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Office", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.jobLocation = "Home" },
                                    modifier = Modifier.weight(1f).height(45.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (viewModel.jobLocation == "Home") Color(0xFF0052D4) else Color.Transparent,
                                        contentColor   = if (viewModel.jobLocation == "Home") Color.White else textColor
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (viewModel.jobLocation == "Home") Color.Transparent else dividerColor
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Home", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // ── 2. BOTÓN PRINCIPAL con animación ─────────────────────
                    Button(
                        onClick = {
                            buttonPressed = true
                            viewModel.onMarkEntryClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .scale(buttonScale),
                        shape  = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        val buttonText = if (viewModel.isPunchedIn) "MARK EXIT" else "MARK ENTRY"
                        Text(
                            buttonText,
                            fontSize      = 18.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = Color.White,
                            letterSpacing = 1.sp
                        )
                    }

                    // ── 3. BOTÓN DE DESCANSO ──────────────────────────────────
                    if (viewModel.isPunchedIn) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Button(
                            onClick = { viewModel.toggleBreak() },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape  = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isOnBreak) Color(0xFFF2994A) else Color(0xFF0052D4)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val breakText = if (viewModel.isOnBreak) "END BREAK" else "START BREAK"
                                Text(
                                    breakText,
                                    fontSize      = 16.sp,
                                    fontWeight    = FontWeight.Bold,
                                    color         = Color.White,
                                    letterSpacing = 1.sp
                                )
                                // Duración del descanso en tiempo real
                                if (viewModel.isOnBreak) {
                                    Text(
                                        viewModel.breakDuration,
                                        fontSize   = 13.sp,
                                        color      = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // ── 4. BARRA DE PROGRESO DE HORAS ────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = cardColor),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Daily Progress",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp,
                                    color      = textColor
                                )
                                Text(
                                    "${viewModel.timeElapsed} / 8h",
                                    fontSize = 13.sp,
                                    color    = textColor.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress         = { animatedProgress },
                                modifier         = Modifier.fillMaxWidth().height(10.dp),
                                color            = when {
                                    animatedProgress >= 1f  -> Color(0xFFFF5252)
                                    animatedProgress >= 0.75f -> Color(0xFFF2994A)
                                    else                    -> Color(0xFF1BD176)
                                },
                                trackColor       = dividerColor,
                                strokeCap        = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when {
                                    animatedProgress >= 1f  -> "🎉 Workday complete!"
                                    animatedProgress >= 0.75f -> "Almost there! Keep going."
                                    viewModel.isPunchedIn   -> "Working towards your 8h goal."
                                    else                    -> "Clock in to start tracking."
                                },
                                fontSize = 12.sp,
                                color    = textColor.copy(alpha = 0.55f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── 5. TARJETA DE RESUMEN ─────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(containerColor = cardColor),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Entrance: ${viewModel.entranceTime}",
                                fontWeight = FontWeight.Bold,
                                color      = textColor,
                                modifier   = Modifier.padding(20.dp)
                            )
                            HorizontalDivider(color = dividerColor, thickness = 1.dp)
                            Text(
                                "Exit: ${viewModel.exitTime}",
                                fontWeight = FontWeight.Bold,
                                color      = textColor,
                                modifier   = Modifier.padding(20.dp)
                            )
                            HorizontalDivider(color = dividerColor, thickness = 1.dp)
                            Text(
                                "Time Elapsed: ${viewModel.timeElapsed}",
                                fontWeight = FontWeight.Bold,
                                color      = textColor,
                                modifier   = Modifier.padding(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}