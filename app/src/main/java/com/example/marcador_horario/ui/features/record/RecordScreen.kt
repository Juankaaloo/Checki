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
fun RecordScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("home/Luis") } // Ajustar según tu lógica de nombre
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFD9D9D9)) // Fondo gris claro del boceto
        ) {
            // CABECERA AZUL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("RECORD", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // SELECTOR DE MES
                Card(
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Month: February", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // LISTA DE REGISTROS
                LazyColumn {
                    item { RecordCard("Monday, February 3", "08:00 AM", "16:00 PM", "07h 00m") }
                    item { Spacer(modifier = Modifier.height(15.dp)) }
                    item { RecordCard("Tuesday, February 10", "10:00 AM", "18:00 PM", "07h 00m") }
                }
            }
        }
    }
}

@Composable
fun RecordCard(date: String, entrance: String, exit: String, elapsed: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Franja verde lateral
            Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(Color(0xFF00A313)))

            Column(modifier = Modifier.padding(15.dp)) {
                Text(date, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color.Black)

                Text(text = "Entrance: $entrance", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Exit: $exit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Time Elapsed: $elapsed", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}