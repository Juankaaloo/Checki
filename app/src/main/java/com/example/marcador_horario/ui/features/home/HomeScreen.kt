package com.example.marcador_horario.ui.features.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    viewModel: HomeViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(username) {
        viewModel.userName = username
        viewModel.attachContext(context)
    }

    // ── Colores base ──────────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF0D0D0D) else Color(0xFFF0F4FF)
    val cardColor    = if (isDarkMode) Color(0xFF1A1A2E) else Color.White
    val textColor    = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
    val subTextColor = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
    val dividerColor = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8)

    // Gradiente del header adaptativo
    val headerGradient = if (isDarkMode)
        listOf(Color(0xFF1A1A6E), Color(0xFF0D47A1), Color(0xFF0D0D0D))
    else
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFFF0F4FF))

    // ── Animación entrada de pantalla ─────────────────────────────────────────
    var screenVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { screenVisible = true }
    val screenAlpha by animateFloatAsState(
        targetValue = if (screenVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "screenAlpha"
    )
    val screenSlide by animateFloatAsState(
        targetValue = if (screenVisible) 0f else 40f,
        animationSpec = tween(durationMillis = 500, easing = EaseOutCubic),
        label = "screenSlide"
    )

    // ── Animación del botón principal ─────────────────────────────────────────
    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        finishedListener = { buttonPressed = false },
        label = "buttonScale"
    )

    // ── Color animado del botón principal ─────────────────────────────────────
    val buttonColor by animateColorAsState(
        targetValue = if (viewModel.isPunchedIn) Color(0xFFEF5350) else Color(0xFF26A69A),
        animationSpec = tween(durationMillis = 500),
        label = "buttonColor"
    )
    val buttonGlow by animateColorAsState(
        targetValue = if (viewModel.isPunchedIn)
            Color(0xFFEF5350).copy(alpha = 0.35f)
        else
            Color(0xFF26A69A).copy(alpha = 0.35f),
        animationSpec = tween(durationMillis = 500),
        label = "buttonGlow"
    )

    // ── Pulsación del indicador de estado ─────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue  = 1.15f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // ── Color del chip de estado ──────────────────────────────────────────────
    val chipColor = when (viewModel.workStatus) {
        "Working"  -> Color(0xFF26A69A)
        "On Break" -> Color(0xFFFFA726)
        else       -> Color(0xFF78909C)
    }
    val animatedChipColor by animateColorAsState(
        targetValue = chipColor,
        animationSpec = tween(400),
        label = "chipColor"
    )

    // ── Progreso animado ──────────────────────────────────────────────────────
    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.workProgress,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "progress"
    )
    val progressColor by animateColorAsState(
        targetValue = when {
            animatedProgress >= 1f    -> Color(0xFFEF5350)
            animatedProgress >= 0.75f -> Color(0xFFFFA726)
            else                      -> Color(0xFF26A69A)
        },
        animationSpec = tween(400),
        label = "progressColor"
    )

    Scaffold(
        bottomBar = {
            // ── NAV BAR con indicador animado ─────────────────────────────────
            Surface(
                color         = cardColor,
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
                                Icons.Filled.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = subTextColor
                            )
                        },
                        selected = false,
                        onClick  = { navController.navigate("record") },
                        colors   = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
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
                        colors   = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
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

            // ── CAPA 1: HEADER con gradiente ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .background(brush = Brush.verticalGradient(colors = headerGradient))
            ) {
                // Círculos decorativos de fondo (glassmorphism effect)
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .offset(x = (-60).dp, y = (-40).dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.07f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 50.dp, y = 20.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 85.dp)
                        .offset(y = screenSlide.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter      = painterResource(id = R.drawable.checkiii),
                        contentDescription = "App Logo",
                        modifier     = Modifier.width(145.dp).height(52.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Saludo
                    Text(
                        "Welcome back,",
                        color    = Color.White.copy(alpha = 0.75f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        viewModel.userName,
                        color      = Color.White,
                        fontSize   = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        viewModel.currentDate.replaceFirstChar { it.uppercase() },
                        color    = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        letterSpacing = 0.4.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Reloj grande
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            viewModel.currentTime,
                            color      = Color.White,
                            fontSize   = 62.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            " ${viewModel.currentAmPm}",
                            color    = Color.White.copy(alpha = 0.75f),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 10.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── CAPA 2: TARJETA INFERIOR ──────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.66f)
                    .align(Alignment.BottomCenter),
                shape           = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color           = bgColor,
                shadowElevation = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp)
                        .padding(top = 28.dp, bottom = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── CHIP DE ESTADO animado ────────────────────────────────
                    AnimatedContent(
                        targetState = viewModel.workStatus,
                        transitionSpec = {
                            (slideInVertically { -it } + fadeIn()) togetherWith
                                    (slideOutVertically { it } + fadeOut())
                        },
                        label = "statusChip"
                    ) { status ->
                        Surface(
                            shape    = RoundedCornerShape(50.dp),
                            color    = animatedChipColor.copy(alpha = 0.12f),
                            border   = BorderStroke(1.dp, animatedChipColor.copy(alpha = 0.3f)),
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Indicador pulsante
                                Box(
                                    modifier = Modifier.size(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .scale(if (viewModel.isPunchedIn) pulseScale else 1f)
                                            .background(
                                                color = animatedChipColor.copy(
                                                    alpha = if (viewModel.isPunchedIn) pulseAlpha * 0.35f else 0.2f
                                                ),
                                                shape = CircleShape
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .background(animatedChipColor, CircleShape)
                                    )
                                }
                                Text(
                                    text       = status,
                                    color      = animatedChipColor,
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // ── 1. TARJETA DE UBICACIÓN ───────────────────────────────
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        shape     = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                "Work Location",
                                fontWeight    = FontWeight.SemiBold,
                                fontSize      = 13.sp,
                                color         = subTextColor,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val locations = listOf("Office" to Icons.Filled.LocationOn, "Home" to Icons.Filled.Home)
                                locations.forEach { (label, icon) ->
                                    val isSelected = viewModel.jobLocation == label
                                    val locScale by animateFloatAsState(
                                        targetValue = if (isSelected) 1f else 0.97f,
                                        label = "locScale_$label"
                                    )
                                    val locBgColor by animateColorAsState(
                                        targetValue = if (isSelected) Color(0xFF1565C0) else Color.Transparent,
                                        animationSpec = tween(300),
                                        label = "locBg_$label"
                                    )
                                    Button(
                                        onClick  = { viewModel.jobLocation = label },
                                        modifier = Modifier.weight(1f).height(50.dp).scale(locScale),
                                        colors   = ButtonDefaults.buttonColors(
                                            containerColor = locBgColor,
                                            contentColor   = if (isSelected) Color.White else subTextColor
                                        ),
                                        border = if (!isSelected) BorderStroke(1.dp, dividerColor) else null,
                                        shape  = RoundedCornerShape(14.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = if (isSelected) 4.dp else 0.dp
                                        )
                                    ) {
                                        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Text(
                                            label,
                                            fontSize   = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── 2. BOTÓN PRINCIPAL ────────────────────────────────────
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Sombra/glow detrás del botón
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .shadow(
                                    elevation    = 20.dp,
                                    shape        = RoundedCornerShape(18.dp),
                                    ambientColor = buttonGlow,
                                    spotColor    = buttonGlow
                                )
                        )
                        Button(
                            onClick = {
                                buttonPressed = true
                                viewModel.onMarkEntryClick()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(62.dp)
                                .scale(buttonScale),
                            shape  = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                verticalAlignment    = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isPunchedIn)
                                        Icons.Filled.ExitToApp
                                    else
                                        Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint     = Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                AnimatedContent(
                                    targetState = viewModel.isPunchedIn,
                                    transitionSpec = {
                                        (slideInVertically { -it } + fadeIn()) togetherWith
                                                (slideOutVertically { it } + fadeOut())
                                    },
                                    label = "btnText"
                                ) { punchedIn ->
                                    Text(
                                        text         = if (punchedIn) "MARK EXIT" else "MARK ENTRY",
                                        fontSize     = 16.sp,
                                        fontWeight   = FontWeight.ExtraBold,
                                        color        = Color.White,
                                        letterSpacing = 2.sp
                                    )
                                }
                            }
                        }
                    }

                    // ── 3. BOTÓN DE DESCANSO ──────────────────────────────────
                    AnimatedVisibility(
                        visible = viewModel.isPunchedIn,
                        enter   = expandVertically() + fadeIn(),
                        exit    = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            val breakBg by animateColorAsState(
                                targetValue = if (viewModel.isOnBreak)
                                    Color(0xFFFFA726).copy(alpha = 0.15f)
                                else
                                    cardColor,
                                animationSpec = tween(300),
                                label = "breakBg"
                            )
                            val breakBorder by animateColorAsState(
                                targetValue = if (viewModel.isOnBreak)
                                    Color(0xFFFFA726)
                                else
                                    dividerColor,
                                animationSpec = tween(300),
                                label = "breakBorder"
                            )
                            OutlinedButton(
                                onClick  = { viewModel.toggleBreak() },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    containerColor = breakBg
                                ),
                                border = BorderStroke(1.5.dp, breakBorder)
                            ) {
                                Icon(
                                    Icons.Filled.FreeBreakfast,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint     = if (viewModel.isOnBreak) Color(0xFFFFA726) else subTextColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text         = if (viewModel.isOnBreak) "END BREAK" else "START BREAK",
                                        fontSize     = 14.sp,
                                        fontWeight   = FontWeight.Bold,
                                        color        = if (viewModel.isOnBreak) Color(0xFFFFA726) else subTextColor,
                                        letterSpacing = 1.5.sp
                                    )
                                    if (viewModel.isOnBreak) {
                                        Text(
                                            viewModel.breakDuration,
                                            fontSize = 11.sp,
                                            color    = Color(0xFFFFA726).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── 4. BARRA DE PROGRESO ──────────────────────────────────
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        shape     = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Daily Progress",
                                    fontWeight    = FontWeight.SemiBold,
                                    fontSize      = 13.sp,
                                    color         = subTextColor,
                                    letterSpacing = 0.8.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = progressColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        "${viewModel.timeElapsed} / 8h",
                                        fontSize   = 12.sp,
                                        color      = progressColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress  = { animatedProgress },
                                modifier  = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                                color     = progressColor,
                                trackColor = dividerColor,
                                strokeCap  = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AnimatedContent(
                                targetState = when {
                                    animatedProgress >= 1f    -> "🎉 Workday complete!"
                                    animatedProgress >= 0.75f -> "⚡ Almost there! Keep going."
                                    viewModel.isPunchedIn     -> "🚀 Working towards your 8h goal."
                                    else                      -> "⏱ Clock in to start tracking."
                                },
                                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                                label = "progressMsg"
                            ) { msg ->
                                Text(
                                    text     = msg,
                                    fontSize = 12.sp,
                                    color    = subTextColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── 5. TARJETA DE RESUMEN ─────────────────────────────────
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        shape     = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            SummaryRow(
                                label    = "Entrance",
                                value    = viewModel.entranceTime,
                                color    = Color(0xFF26A69A),
                                textColor = textColor
                            )
                            HorizontalDivider(color = dividerColor, thickness = 1.dp)
                            SummaryRow(
                                label    = "Exit",
                                value    = viewModel.exitTime,
                                color    = Color(0xFFEF5350),
                                textColor = textColor
                            )
                            HorizontalDivider(color = dividerColor, thickness = 1.dp)
                            SummaryRow(
                                label    = "Time Elapsed",
                                value    = viewModel.timeElapsed,
                                color    = Color(0xFF42A5F5),
                                textColor = textColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

//  Componente reutilizable para filas de resumen
@Composable
private fun SummaryRow(
    label: String,
    value: String,
    color: Color,
    textColor: Color
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Text(
                label,
                fontWeight    = FontWeight.Medium,
                fontSize      = 14.sp,
                color         = textColor.copy(alpha = 0.65f),
                letterSpacing = 0.3.sp
            )
        }
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize   = 14.sp,
            color      = textColor
        )
    }
}