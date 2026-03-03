package com.example.marcador_horario.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController,
    username: String,
    isDarkMode: Boolean, // <-- Ahora lo recibe de fuera
    viewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(username) {
        viewModel.userName = username
    }

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFEEEEEE)
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val iconColor = if (isDarkMode) Color.LightGray else Color.Black
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = cardColor) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("record") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = iconColor) },
                    selected = false,
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFF2196F3))) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("Welcome, ${viewModel.userName}", color = Color.White, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(viewModel.currentDate.replaceFirstChar { it.uppercase() }, color = Color.White, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(viewModel.currentTime, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Medium)
                    Text(" ${viewModel.currentAmPm}", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(bottom = 12.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp), color = bgColor
                ) {
                    Column(modifier = Modifier.padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { }, modifier = Modifier.fillMaxWidth().height(75.dp),
                            shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BD176))
                        ) { Text("MARK ENTRY", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp) }

                        Spacer(modifier = Modifier.height(25.dp))

                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Job Location:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                                Spacer(modifier = Modifier.height(15.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = textColor)
                                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = textColor)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Entrance: --", fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(20.dp))
                                HorizontalDivider(color = dividerColor, thickness = 1.dp)
                                Text("Exit: --", fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(20.dp))
                                HorizontalDivider(color = dividerColor, thickness = 1.dp)
                                Text("Time Elapsed: --", fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.padding(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}