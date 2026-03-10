package com.example.marcador_horario.ui.features.settings

import com.example.marcador_horario.ui.features.settings.SettingsViewModel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean,
    isAdmin: Boolean = false,
    onThemeChange: (Boolean) -> Unit,
    viewModel: SettingsViewModel
) {
    // ── Estado ────────────────────────────────────────────────────────────────
    var notificationsEnabled by remember { mutableStateOf(viewModel.notifEnabled) }
    var gpsEnabled           by remember { mutableStateOf(viewModel.gpsEnabled) }
    var selectedLanguage     by remember { mutableStateOf("English") }
    var avatarUri            by remember { mutableStateOf<Uri?>(null) }

    // ── Diálogos ──────────────────────────────────────────────────────────────
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showReportDialog   by remember { mutableStateOf(false) }
    var showLogOutDialog   by remember { mutableStateOf(false) }
    var showAboutDialog    by remember { mutableStateOf(false) }
    var reportReason       by remember { mutableStateOf("Cannot clock in") }
    var reportDetails      by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri
            coroutineScope.launch { snackbarHostState.showSnackbar("Profile picture updated ✓") }
        }
    }

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

    val roleLabel  = if (isAdmin) "Administrator" else "Employee"
    val roleColor  = if (isAdmin) Color(0xFFFFA726) else Color(0xFF26A69A)
    val roleBg     = roleColor.copy(alpha = 0.12f)

    // ── Animación de entrada ──────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val cardSlide by animateFloatAsState(
        targetValue = if (visible) 0f else 70f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "cardSlide"
    )
    val avatarScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "avatarScale"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isAdmin) {
                Surface(color = cardColor, shadowElevation = 16.dp) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        modifier       = Modifier.height(65.dp)
                    ) {
                        NavigationBarItem(
                            icon     = { Icon(Icons.Filled.Home, contentDescription = null, tint = subTextColor, modifier = Modifier.size(24.dp)) },
                            selected = false,
                            onClick  = { navController.navigate("home") },
                            colors   = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                        NavigationBarItem(
                            icon     = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = subTextColor, modifier = Modifier.size(24.dp)) },
                            selected = false,
                            onClick  = { navController.navigate("record") },
                            colors   = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                        NavigationBarItem(
                            icon     = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(26.dp)) },
                            selected = true,
                            onClick  = {},
                            colors   = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF42A5F5).copy(alpha = 0.15f)
                            )
                        )
                    }
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
                        .size(220.dp)
                        .offset(x = (-60).dp, y = (-50).dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 50.dp, y = 10.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                if (isAdmin) {
                    IconButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(top = 40.dp, start = 12.dp)
                            .align(Alignment.TopStart)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 85.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Settings",
                        color         = Color.White,
                        fontSize      = 28.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "Manage your preferences",
                        color    = Color.White.copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // ── TARJETA DE PERFIL ─────────────────────────────────────
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth(0.88f)
                            .scale(avatarScale),
                        shape     = RoundedCornerShape(22.dp),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar con badge de edición
                            Box(
                                modifier         = Modifier.size(64.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color(0xFF42A5F5).copy(alpha = 0.2f), Color(0xFF1565C0).copy(alpha = 0.1f))
                                            )
                                        )
                                        .border(2.dp, Color(0xFF42A5F5), CircleShape)
                                        .clickable { galleryLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (avatarUri != null) {
                                        AsyncImage(
                                            model              = avatarUri,
                                            contentDescription = "Profile picture",
                                            contentScale       = ContentScale.Crop,
                                            modifier           = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.Person,
                                            contentDescription = null,
                                            tint     = Color(0xFF42A5F5),
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }
                                // Badge edición
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1565C0))
                                        .clickable { galleryLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Edit photo",
                                        tint     = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    username,
                                    fontWeight    = FontWeight.ExtraBold,
                                    fontSize      = 18.sp,
                                    color         = textColor,
                                    letterSpacing = 0.2.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    shape  = RoundedCornerShape(50.dp),
                                    color  = roleBg,
                                    modifier = Modifier
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            if (isAdmin) Icons.Filled.AdminPanelSettings else Icons.Filled.Badge,
                                            contentDescription = null,
                                            tint     = roleColor,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            roleLabel,
                                            fontSize   = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = roleColor
                                        )
                                    }
                                }
                            }

                            // Flecha de perfil
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = subTextColor,
                                modifier = Modifier.size(20.dp)
                            )
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
                        .padding(top = 28.dp, bottom = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ── SECCIÓN: PREFERENCIAS ─────────────────────────────────
                    SectionLabel("Preferences", subTextColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column {
                            SettingSwitchItem(
                                icon        = Icons.Filled.Notifications,
                                iconBg      = Color(0xFF42A5F5),
                                title       = "Notifications",
                                subtitle    = "Push alerts & reminders",
                                state       = notificationsEnabled,
                                textColor   = textColor,
                                subColor    = subTextColor,
                                dividerColor = dividerColor
                            ) {
                                notificationsEnabled = it
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Notifications ${if (it) "enabled" else "disabled"} ✓"
                                    )
                                }
                            }
                            SettingClickableItem(
                                icon      = Icons.Filled.Language,
                                iconBg    = Color(0xFF26A69A),
                                title     = "Language",
                                subtitle  = "App display language",
                                value     = selectedLanguage,
                                textColor = textColor,
                                subColor  = subTextColor,
                                dividerColor = dividerColor
                            ) { showLanguageDialog = true }

                            SettingSwitchItem(
                                icon        = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                iconBg      = if (isDarkMode) Color(0xFF7986CB) else Color(0xFFFFA726),
                                title       = "Dark Mode",
                                subtitle    = if (isDarkMode) "Currently dark theme" else "Currently light theme",
                                state       = isDarkMode,
                                textColor   = textColor,
                                subColor    = subTextColor,
                                dividerColor = Color.Transparent,
                                showDivider = false
                            ) {
                                onThemeChange(it)
                                coroutineScope.launch { snackbarHostState.showSnackbar("Theme changed ✓") }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── SECCIÓN: PRIVACY ──────────────────────────────────────
                    SectionLabel("Privacy & Location", subTextColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        SettingSwitchItem(
                            icon        = Icons.Filled.MyLocation,
                            iconBg      = Color(0xFFEF5350),
                            title       = "GPS Tracking",
                            subtitle    = "Location-based clock-in",
                            state       = gpsEnabled,
                            textColor   = textColor,
                            subColor    = subTextColor,
                            dividerColor = Color.Transparent,
                            showDivider = false
                        ) {
                            gpsEnabled = it
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "GPS ${if (it) "enabled" else "disabled"} ✓"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── SECCIÓN: SUPPORT ──────────────────────────────────────
                    SectionLabel("Support & Info", subTextColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column {
                            SettingClickableItem(
                                icon      = Icons.Filled.Assessment,
                                iconBg    = Color(0xFF7986CB),
                                title     = "Reports",
                                subtitle  = "Submit an incident report",
                                textColor = textColor,
                                subColor  = subTextColor,
                                dividerColor = dividerColor
                            ) { showReportDialog = true }

                            SettingClickableItem(
                                icon      = Icons.Filled.Info,
                                iconBg    = Color(0xFF26A69A),
                                title     = "About",
                                subtitle  = "App version & details",
                                textColor = textColor,
                                subColor  = subTextColor,
                                dividerColor = Color.Transparent,
                                showDivider = false
                            ) { showAboutDialog = true }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── LOG OUT ───────────────────────────────────────────────
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(20.dp),
                        colors    = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF5350).copy(alpha = 0.08f)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogOutDialog = true }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFFEF5350).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    tint     = Color(0xFFEF5350),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Log Out",
                                    color      = Color(0xFFEF5350),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize   = 15.sp
                                )
                                Text(
                                    "Sign out of your account",
                                    color    = Color(0xFFEF5350).copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFFEF5350).copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DIÁLOGOS MEJORADOS
    // ═══════════════════════════════════════════════════════════════════════════

    // ── Log Out ───────────────────────────────────────────────────────────────
    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1A1A2E) else Color.White,
            shape            = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0xFFEF5350).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint     = Color(0xFFEF5350),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    "Log Out",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 20.sp,
                    color      = if (isDarkMode) Color.White else Color(0xFF1A1A2E),
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    "Are you sure you want to sign out? You'll need your credentials to access the app again.",
                    color     = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0),
                    fontSize  = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier  = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogOutDialog = false
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { showLogOutDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Stay",
                        color      = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF1565C0),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )
    }

    // ── Idioma ────────────────────────────────────────────────────────────────
    if (showLanguageDialog) {
        val dialogText = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
        val languages  = listOf(
            "English" to "🇬🇧",
            "Español" to "🇪🇸",
            "Français" to "🇫🇷",
            "Deutsch" to "🇩🇪"
        )
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1A1A2E) else Color.White,
            shape            = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF26A69A).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Language,
                        contentDescription = null,
                        tint     = Color(0xFF26A69A),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Select Language",
                    fontWeight = FontWeight.ExtraBold,
                    color      = dialogText,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    languages.forEach { (lang, flag) ->
                        val isSelected = lang == selectedLanguage
                        val rowBg by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF26A69A).copy(alpha = 0.1f) else Color.Transparent,
                            label = "langBg_$lang"
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Language set to $lang ✓")
                                    }
                                },
                            color = rowBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(flag, fontSize = 22.sp)
                                Text(
                                    lang,
                                    color      = dialogText,
                                    fontSize   = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier   = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint     = Color(0xFF26A69A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", color = Color(0xFF7E8CB0))
                }
            }
        )
    }

    // ── Reporte ───────────────────────────────────────────────────────────────
    if (showReportDialog) {
        val dialogText = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
        val reasons    = listOf(
            "Cannot clock in"    to Icons.Filled.AccessTime,
            "Medical appointment" to Icons.Filled.LocalHospital,
            "System error"       to Icons.Filled.BugReport,
            "Other"              to Icons.Filled.MoreHoriz
        )
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1A1A2E) else Color.White,
            shape            = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF7986CB).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Assessment,
                        contentDescription = null,
                        tint     = Color(0xFF7986CB),
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    "Send a Report",
                    fontWeight = FontWeight.ExtraBold,
                    color      = dialogText,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    Text(
                        "What's the reason?",
                        color      = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0),
                        fontSize   = 13.sp,
                        modifier   = Modifier.padding(bottom = 8.dp)
                    )
                    reasons.forEach { (reason, icon) ->
                        val isSelected = reason == reportReason
                        val rowBg by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF7986CB).copy(alpha = 0.1f) else Color.Transparent,
                            label = "reasonBg_$reason"
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { reportReason = reason },
                            color = rowBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint     = if (isSelected) Color(0xFF7986CB) else
                                        if (isDarkMode) Color(0xFF7986CB).copy(alpha = 0.5f) else Color(0xFFB0BEC5),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    reason,
                                    color      = dialogText,
                                    fontSize   = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier   = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint     = Color(0xFF7986CB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value         = reportDetails,
                        onValueChange = { reportDetails = it },
                        label         = { Text("Additional details (optional)", fontSize = 13.sp) },
                        modifier      = Modifier.fillMaxWidth().height(100.dp),
                        shape         = RoundedCornerShape(14.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedTextColor      = dialogText,
                            unfocusedTextColor    = dialogText,
                            focusedBorderColor    = Color(0xFF7986CB),
                            unfocusedBorderColor  = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8),
                            focusedLabelColor     = Color(0xFF7986CB)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        reportDetails    = ""
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Report sent successfully ✓")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB))
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Report", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = Color(0xFF7E8CB0))
                }
            }
        )
    }

    // ── About ─────────────────────────────────────────────────────────────────
    if (showAboutDialog) {
        val dialogText = if (isDarkMode) Color(0xFFE8EAF6) else Color(0xFF1A1A2E)
        val subColor   = if (isDarkMode) Color(0xFF7986CB) else Color(0xFF7E8CB0)
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1A1A2E) else Color.White,
            shape            = RoundedCornerShape(24.dp),
            icon = {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            },
            title = {
                Text(
                    "Marcador Horario",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 20.sp,
                    color      = dialogText,
                    textAlign  = TextAlign.Center,
                    modifier   = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AboutInfoRow(Icons.Filled.NewReleases,  "Version",   "1.0.0",        dialogText, subColor)
                    AboutInfoRow(Icons.Filled.Build,        "Build",     "2025.06",      dialogText, subColor)
                    AboutInfoRow(Icons.Filled.Business,     "Developer", "Your Company", dialogText, subColor)
                    AboutInfoRow(Icons.Filled.PhoneAndroid, "Platform",  "Android",      dialogText, subColor)
                    HorizontalDivider(
                        color     = if (isDarkMode) Color(0xFF2A2A4A) else Color(0xFFE8ECF8),
                        modifier  = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        "© 2025 Marcador Horario\nAll rights reserved.",
                        fontSize  = 11.sp,
                        color     = subColor,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth(),
                        lineHeight = 17.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick  = { showAboutDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ── Etiqueta de sección ───────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String, color: Color) {
    Text(
        text.uppercase(),
        fontSize      = 11.sp,
        fontWeight    = FontWeight.Bold,
        color         = color,
        letterSpacing = 1.2.sp,
        modifier      = Modifier.padding(start = 4.dp)
    )
}

// ── Item con switch ───────────────────────────────────────────────────────────
@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String = "",
    state: Boolean,
    textColor: Color,
    subColor: Color,
    dividerColor: Color,
    showDivider: Boolean = true,
    onStateChange: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg.copy(alpha = 0.15f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, fontSize = 12.sp, color = subColor)
                }
            }
            val trackColor by animateColorAsState(
                targetValue = if (state) Color(0xFF26A69A) else Color(0xFFB0BEC5),
                animationSpec = tween(300),
                label = "switchTrack"
            )
            Switch(
                checked         = state,
                onCheckedChange = onStateChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White,
                    checkedTrackColor   = trackColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFB0BEC5)
                )
            )
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

// ── Item clickeable ───────────────────────────────────────────────────────────
@Composable
fun SettingClickableItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String = "",
    value: String = "",
    textColor: Color,
    subColor: Color,
    dividerColor: Color,
    showDivider: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg.copy(alpha = 0.15f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, fontSize = 12.sp, color = subColor)
                }
            }
            if (value.isNotEmpty()) {
                Text(
                    value,
                    color    = subColor,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint     = subColor,
                modifier = Modifier.size(20.dp)
            )
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

// ── Fila info del About ───────────────────────────────────────────────────────
@Composable
private fun AboutInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    textColor: Color,
    subColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(icon, contentDescription = null, tint = subColor, modifier = Modifier.size(16.dp))
        Text(label, fontSize = 13.sp, color = subColor, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

// ── AboutRow legacy (compatibilidad) ─────────────────────────────────────────
@Composable
fun AboutRow(label: String, value: String, textColor: Color, subColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = subColor)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}