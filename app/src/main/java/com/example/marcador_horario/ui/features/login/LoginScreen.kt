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

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: (String) -> Unit // <-- Ahora pasamos el nombre
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)
                .background(brush = Brush.verticalGradient(colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))))
        )

        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f).align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text("LOGIN", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFF222222))

                Spacer(modifier = Modifier.height(25.dp))
                LoginTextField("Employee email", viewModel.email, { viewModel.onEmailChanged(it) }, "Enter your email")

                Spacer(modifier = Modifier.height(16.dp))
                LoginTextField("Password", viewModel.password, { viewModel.onPasswordChanged(it) }, "Enter your password", true, viewModel.passwordVisible) {
                    viewModel.togglePasswordVisibility()
                }

                Text(
                    "You forget your password?",
                    color = Color(0xFF0072FF),
                    fontSize = 13.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.End).padding(top = 10.dp).clickable { },
                    textDecoration = TextDecoration.Underline
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Checkbox(checked = viewModel.acceptedTerms, onCheckedChange = { viewModel.onTermsChanged(it) }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0072FF)))
                    Text("Accept terms and conditions", fontSize = 14.sp, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onLoginClick(onLoginSuccess) }, // <-- Enviamos el éxito con el nombre
                    modifier = Modifier.fillMaxWidth(0.85f).height(55.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4))
                ) {
                    Text("Enter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Image(painter = painterResource(id = R.drawable.checkiii), contentDescription = null, modifier = Modifier.fillMaxWidth(0.7f).height(200.dp), contentScale = ContentScale.Fit)
        }
    }
}

@Composable
fun LoginTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, isPassword: Boolean = false, passwordVisible: Boolean = false, onVisibilityChange: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(bottom = 6.dp))
        TextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = { if (isPassword) IconButton(onClick = { onVisibilityChange?.invoke() }) { Text(if (passwordVisible) "🙈" else "👁️", fontSize = 18.sp) } },
            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF1F1F1), unfocusedContainerColor = Color(0xFFF1F1F1), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
    }
}