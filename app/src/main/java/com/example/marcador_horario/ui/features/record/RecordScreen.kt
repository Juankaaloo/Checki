package com.example.marcador_horario.ui.features.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RecordScreen(
    navController: NavController,
    isDarkMode: Boolean
) {
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFD9D9D9)
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconColor = if (isDarkMode) Color.LightGray else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = cardColor) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("home") } // <-- Ruta arreglada
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(bgColor)) {
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF2196F3)).padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) { Text("RECORD", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold) }

            Column(modifier = Modifier.padding(20.dp)) {
                Card(
                    modifier = Modifier.width(200.dp), shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Month: February", fontWeight = FontWeight.Medium, color = textColor)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn {
                    item { RecordCard("Monday, February 3", "08:00 AM", "16:00 PM", "07h 00m", cardColor, textColor, dividerColor) }
                    item { Spacer(modifier = Modifier.height(15.dp)) }
                    item { RecordCard("Tuesday, February 10", "10:00 AM", "18:00 PM", "07h 00m", cardColor, textColor, dividerColor) }
                }
            }
        }
    }
}

@Composable
fun RecordCard(date: String, entrance: String, exit: String, elapsed: String, cardColor: Color, textColor: Color, dividerColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(Color(0xFF00A313)))

            Column(modifier = Modifier.padding(15.dp)) {
                Text(date, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor, modifier = Modifier.align(Alignment.CenterHorizontally))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = dividerColor)

                Text(text = "Entrance: $entrance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Exit: $exit", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Time Elapsed: $elapsed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            }
        }
    }
}