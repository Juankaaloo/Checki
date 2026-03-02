package com.example.marcador_horario.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
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
    navController: NavController, // <-- IMPORTANTE
    username: String,             // <-- IMPORTANTE
    viewModel: HomeViewModel = viewModel()
) {
    // Sincronizamos el nombre con el ViewModel
    LaunchedEffect(username) {
        viewModel.userName = username
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null, tint = Color(0xFF0052D4)) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("record") } // Navegación activada
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.Black) },
                    selected = false,
                    onClick = { navController.navigate("settings") } // Navegación activada
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF2196F3))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("Welcome, ${viewModel.userName}", color = Color.White, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(viewModel.currentDate, color = Color.White, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(viewModel.currentTime, color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Medium)
                    Text(" ${viewModel.currentAmPm}", color = Color.White, fontSize = 24.sp, modifier = Modifier.padding(bottom = 12.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    color = Color(0xFFEEEEEE)
                ) {
                    Column(
                        modifier = Modifier.padding(25.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { /* Próxima funcionalidad */ },
                            modifier = Modifier.fillMaxWidth().height(75.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BD176))
                        ) {
                            Text("MARK ENTRY", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Job Location:", fontWeight = FontWeight.Bold, color = Color.Black)
                                Spacer(modifier = Modifier.height(15.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Black)
                                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Entrance: --", fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                                HorizontalDivider(color = Color.LightGray)
                                Text("Exit: --", fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                                HorizontalDivider(color = Color.LightGray)
                                Text("Time Elapsed: --", fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}