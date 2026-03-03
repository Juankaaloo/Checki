package com.example.marcador_horario.ui.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marcador_horario.R

/**
 * Pantalla principal de Autenticación (Login).
 * Constituye el punto de entrada a la aplicación. Su diseño se basa en un layout
 * dividido: un fondo superior con gradiente y una tarjeta interactiva superpuesta (Surface).
 *
 * @param viewModel Inyectado automáticamente por Compose. Contiene la lógica de validación
 * y mantiene el estado de los campos de texto, separando la UI de la lógica de negocio.
 * @param onLoginSuccess Callback que se ejecuta cuando las credenciales son válidas.
 * Envía al NavGraph el nombre de usuario y un flag booleano (isAdmin).
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: (String, Boolean) -> Unit
) {
    // Contenedor principal tipo Box para permitir la superposición de capas (Z-Index)
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // --- CAPA 1: FONDO SUPERIOR (Gradiente) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f) // Ocupa la mitad superior de la pantalla
                .background(
                    // Aplicamos un degradado vertical para un acabado más moderno e inmersivo
                    brush = Brush.verticalGradient(colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))
                )
        )

        // --- CAPA 2: TARJETA DE FORMULARIO ---
        // Se ancla a la parte inferior (BottomCenter) y se superpone al fondo
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            color = Color.White,
            shadowElevation = 8.dp // Sombra para enfatizar la superposición
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text("LOGIN", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFF222222))

                Spacer(modifier = Modifier.height(25.dp))

                // --- CAMPOS DE ENTRADA (Data Binding) ---
                // Los TextFields envían el texto tecleado al ViewModel y se redibujan con el nuevo valor
                LoginTextField(
                    label = "Employee email",
                    value = viewModel.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    placeholder = "Enter your email"
                )

                Spacer(modifier = Modifier.height(16.dp))

                LoginTextField(
                    label = "Password",
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    placeholder = "Enter your password",
                    isPassword = true,
                    passwordVisible = viewModel.passwordVisible,
                    onVisibilityChange = { viewModel.togglePasswordVisibility() }
                )

                // Enlace de recuperación de contraseña (Visual)
                Text(
                    "You forget your password?",
                    color = Color(0xFF0072FF),
                    fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End).padding(top = 10.dp).clickable { },
                    textDecoration = TextDecoration.Underline
                )

                // Checkbox de Términos y Condiciones
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Checkbox(
                        checked = viewModel.acceptedTerms,
                        onCheckedChange = { viewModel.onTermsChanged(it) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0072FF))
                    )
                    Text("Accept terms and conditions", fontSize = 14.sp, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia el fondo de la tarjeta

                // --- BOTÓN DE ENVÍO (CTA) ---
                Button(
                    onClick = { viewModel.onLoginClick(onLoginSuccess) }, // Delegamos la validación al ViewModel
                    modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4))
                ) {
                    Text("Enter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- CAPA 3: ILUSTRACIÓN O LOGO ---
        // Se coloca en el espacio superior disponible, por encima de todo.
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.checkiii),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.7f).height(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

// ==============================================================================
// MICRO-COMPONENTES (Sub-UI)
// ==============================================================================

/**
 * Componente de entrada de texto personalizado y reutilizable (Clean Code).
 * Se encarga de estandarizar el diseño visual de los inputs y maneja dinámicamente
 * la ocultación de contraseñas.
 **/
@Composable
fun LoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityChange: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(bottom = 6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,

            // Lógica de enmascaramiento de contraseña
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,

            // Icono interactivo al final del TextField (Trailing Icon)
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { onVisibilityChange?.invoke() }) {
                        Text(if (passwordVisible) "🙈" else "👁️", fontSize = 18.sp)
                    }
                }
            },

            // Personalización profunda de los colores para adaptarlos a la marca
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1),
                focusedIndicatorColor = Color.Transparent, // Oculta la línea inferior nativa de Material
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}