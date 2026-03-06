package com.example.marcador_horario.ui.features.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
    onThemeChange: (Boolean) -> Unit
) {
    // ─── Estado ───────────────────────────────────────────────────────────────
    var notificationsEnabled by remember { mutableStateOf(true) }
    var gpsEnabled           by remember { mutableStateOf(true) }
    var selectedLanguage     by remember { mutableStateOf("English") }
    var avatarUri            by remember { mutableStateOf<Uri?>(null) }

    // ─── Diálogos ─────────────────────────────────────────────────────────────
    var showLanguageDialog   by remember { mutableStateOf(false) }
    var showReportDialog     by remember { mutableStateOf(false) }
    var showLogOutDialog     by remember { mutableStateOf(false) }
    var showAboutDialog      by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    // ─── Lanzador de galería para avatar ─────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            avatarUri = uri
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Profile picture updated ✓")
            }
        }
    }

    // ─── Paleta ───────────────────────────────────────────────────────────────
    val bgColor      = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor    = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor    = if (isDarkMode) Color.White      else Color.Black
    val iconColor    = if (isDarkMode) Color.LightGray  else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray   else Color.LightGray

    // ─── Colores e icono del rol ──────────────────────────────────────────────
    val roleLabel    = if (isAdmin) "Administrator" else "Employee"
    val roleBg       = if (isAdmin) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
    val roleText     = if (isAdmin) Color(0xFFE65100)  else Color(0xFF2E7D32)
    val roleBgDark   = if (isAdmin) Color(0xFF3E2800)  else Color(0xFF1B3A1F)
    val roleChipBg   = if (isDarkMode) roleBgDark else roleBg

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isAdmin) {
                NavigationBar(containerColor = cardColor) {
                    NavigationBarItem(
                        icon     = { Icon(Icons.Filled.Home,      contentDescription = null, tint = iconColor) },
                        selected = false,
                        onClick  = { navController.navigate("home") }
                    )
                    NavigationBarItem(
                        icon     = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = iconColor) },
                        selected = false,
                        onClick  = { navController.navigate("record") }
                    )
                    NavigationBarItem(
                        icon     = { Icon(Icons.Filled.Settings,  contentDescription = null, tint = Color(0xFF0052D4)) },
                        selected = true,
                        onClick  = { }
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
                if (isAdmin) {
                    IconButton(
                        onClick  = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(top = 40.dp, start = 15.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Configuration", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))

                    // ── TARJETA DE PERFIL ─────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ── AVATAR EDITABLE ───────────────────────────────
                            Box(
                                modifier        = Modifier.size(56.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                // Círculo principal con foto o icono
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFE0E0FF))
                                        .border(2.dp, Color(0xFF0052D4), CircleShape)
                                        .clickable { galleryLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (avatarUri != null) {
                                        AsyncImage(
                                            model            = avatarUri,
                                            contentDescription = "Profile picture",
                                            contentScale     = ContentScale.Crop,
                                            modifier         = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            Icons.Filled.Person,
                                            contentDescription = null,
                                            tint     = Color(0xFF0052D4),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                // Mini botón de edición
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0052D4))
                                        .clickable { galleryLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Edit photo",
                                        tint     = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(username, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                                Spacer(modifier = Modifier.height(5.dp))
                                // ── CHIP DE ROL ───────────────────────────────
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = roleChipBg
                                ) {
                                    Text(
                                        text     = roleLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color    = roleText,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
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
                        .padding(top = 40.dp, bottom = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ── PREFERENCIAS ──────────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column {
                            SettingSwitchItem(Icons.Filled.Notifications, "Notifications", notificationsEnabled, textColor) {
                                notificationsEnabled = it
                                coroutineScope.launch { snackbarHostState.showSnackbar("Notifications ${if (it) "enabled" else "disabled"} ✓") }
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)
                            SettingClickableItem(Icons.Filled.Info, "Language", selectedLanguage, textColor) {
                                showLanguageDialog = true
                            }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)
                            SettingSwitchItem(Icons.Filled.Star, "Dark Mode", isDarkMode, textColor) {
                                onThemeChange(it)
                                coroutineScope.launch { snackbarHostState.showSnackbar("Theme changed ✓") }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // ── GPS ───────────────────────────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        SettingSwitchItem(Icons.Filled.LocationOn, "GPS", gpsEnabled, textColor) {
                            gpsEnabled = it
                            coroutineScope.launch { snackbarHostState.showSnackbar("GPS ${if (it) "enabled" else "disabled"} ✓") }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // ── REPORTES / ABOUT / LOGOUT ─────────────────────────────
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Column {
                            SettingClickableItem(
                                icon = Icons.AutoMirrored.Filled.List,
                                title = "Reports",
                                textColor = textColor,
                                hasArrow = true
                            ) { showReportDialog = true }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)

                            SettingClickableItem(
                                icon = Icons.Filled.Info,
                                title = "About",
                                textColor = textColor,
                                hasArrow = true
                            ) { showAboutDialog = true }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 15.dp), color = dividerColor)

                            // ── LOG OUT con confirmación ──────────────────────
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showLogOutDialog = true }
                                    .padding(15.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252))
                                Spacer(modifier = Modifier.width(15.dp))
                                Text("Log Out", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DIÁLOGOS
    // ═══════════════════════════════════════════════════════════════════════════

    // ── Confirmar Log Out ─────────────────────────────────────────────────────
    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint   = Color(0xFFFF5252),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text("Log Out", fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black)
            },
            text = {
                Text(
                    "Are you sure you want to log out? You'll need to sign in again to access your account.",
                    color    = if (isDarkMode) Color.LightGray else Color(0xFF555555),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogOutDialog = false
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogOutDialog = false }) {
                    Text("Cancel", color = Color(0xFF0052D4))
                }
            }
        )
    }

    // ── Selector de idioma ────────────────────────────────────────────────────
    if (showLanguageDialog) {
        val textCol = if (isDarkMode) Color.White else Color.Black
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            title = { Text("Select Language", color = textCol, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("English", "Español", "Français", "Deutsch").forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Language changed to $lang ✓")
                                    }
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (lang == selectedLanguage),
                                onClick  = {
                                    selectedLanguage = lang
                                    showLanguageDialog = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Language changed to $lang ✓")
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0052D4))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lang, color = textCol, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel", color = Color(0xFFFF5252))
                }
            }
        )
    }

    // ── Enviar reporte ────────────────────────────────────────────────────────
// ── Variables del diálogo (fuera del if para evitar warnings) ────────────
    var reportReason  by remember { mutableStateOf("Cannot clock in") }
    var reportDetails by remember { mutableStateOf("") }

    // ── Enviar reporte ────────────────────────────────────────────────────────
    if (showReportDialog) {
        val textCol = if (isDarkMode) Color.White else Color.Black

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            title = { Text("Send a Report", color = textCol, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select a reason:", color = textCol, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Cannot clock in", "Medical appointment", "System error", "Other").forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = reason }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (reason == reportReason),
                                onClick  = { reportReason = reason },
                                colors   = RadioButtonDefaults.colors(selectedColor = Color(0xFF0052D4))
                            )
                            Text(reason, color = textCol, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value         = reportDetails,
                        onValueChange = { reportDetails = it },
                        label         = { Text("Additional details...") },
                        modifier      = Modifier.fillMaxWidth().height(100.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedTextColor   = textCol,
                            unfocusedTextColor = textCol
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        reportDetails = "" // limpiar al enviar
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Report sent successfully ✓")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4))
                ) {
                    Text("Send Report", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // ── About ─────────────────────────────────────────────────────────────────
    if (showAboutDialog) {
        val textCol = if (isDarkMode) Color.White else Color.Black
        val subCol  = if (isDarkMode) Color.LightGray else Color(0xFF555555)

        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor   = if (isDarkMode) Color(0xFF1E1E1E) else Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint     = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text("Marcador Horario", fontWeight = FontWeight.Bold, color = textCol)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AboutRow("Version",     "1.0.0",          textCol, subCol)
                    AboutRow("Build",       "2025.06",        textCol, subCol)
                    AboutRow("Developer",   "Your Company",   textCol, subCol)
                    AboutRow("Platform",    "Android",        textCol, subCol)
                    HorizontalDivider(color = if (isDarkMode) Color.DarkGray else Color.LightGray)
                    Text(
                        "© 2025 Marcador Horario. All rights reserved.",
                        fontSize  = 11.sp,
                        color     = subCol,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4))
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MICRO-COMPONENTES
// ─────────────────────────────────────────────────────────────────────────────

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

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    state: Boolean,
    textColor: Color,
    onStateChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textColor)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked         = state,
            onCheckedChange = onStateChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00A313)
            )
        )
    }
}

@Composable
fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    value: String = "",
    textColor: Color,
    hasArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textColor)
        Spacer(modifier = Modifier.width(15.dp))
        Text(title, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.weight(1f))
        if (value.isNotEmpty()) {
            Text(value, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
        }
        if (hasArrow) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textColor)
        } else if (value.isEmpty()) {
            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = textColor)
        }
    }
}