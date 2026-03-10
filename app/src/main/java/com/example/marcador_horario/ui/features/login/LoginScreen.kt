package com.example.marcador_horario.ui.features.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marcador_horario.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String, Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    // ── Validaciones en tiempo real ───────────────────────────────────────────
    val isEmailValid = viewModel.email.isEmpty() ||
            android.util.Patterns.EMAIL_ADDRESS.matcher(viewModel.email).matches()
    val isPasswordValid = viewModel.password.isEmpty() || viewModel.password.length >= 4
    val canLogin = viewModel.email.isNotEmpty() &&
            viewModel.password.isNotEmpty() &&
            viewModel.acceptedTerms &&
            isEmailValid &&
            isPasswordValid

    // ── Animación de entrada ──────────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val cardOffsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 80f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "cardSlide"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "logoScale"
    )

    // ── Animación del botón ───────────────────────────────────────────────────
    var btnPressed by remember { mutableStateOf(false) }
    val btnScale by animateFloatAsState(
        targetValue = if (btnPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        finishedListener = { btnPressed = false },
        label = "btnScale"
    )

    val btnColor by animateColorAsState(
        targetValue = if (canLogin) Color(0xFF1565C0) else Color(0xFFB0BEC5),
        animationSpec = tween(300),
        label = "btnColor"
    )

    // ── Shake animation para error ────────────────────────────────────────────
    var loginError by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        targetValue = if (loginError) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 400
            0f  at 0
            -12f at 50
            12f  at 100
            -10f at 150
            10f  at 200
            -6f  at 250
            6f   at 300
            0f   at 400
        },
        finishedListener = { loginError = false },
        label = "shake"
    )

    // ── Trigger shake cuando hay error del backend ────────────────────────────
    LaunchedEffect(viewModel.errorMessage) {
        if (viewModel.errorMessage != null) loginError = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B3E))
    ) {

        // ── CAPA 1: FONDO GRADIENTE ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.52f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0), Color(0xFF0D1B3E))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = (-70).dp, y = (-60).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 60.dp, y = 10.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.04f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }

        // ── CAPA 2: LOGO ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .scale(logoScale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter           = painterResource(id = R.drawable.checkiii),
                contentDescription = null,
                modifier          = Modifier.fillMaxWidth(0.65f).height(190.dp),
                contentScale      = ContentScale.Fit
            )
        }

        // ── CAPA 3: TARJETA FORMULARIO ────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.64f)
                .align(Alignment.BottomCenter)
                .offset(y = cardOffsetY.dp),
            shape           = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color           = Color(0xFFF4F7FF),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter   = fadeIn(tween(700)) + slideInVertically { -30 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Welcome Back",
                            fontSize      = 28.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            color         = Color(0xFF0D1B3E),
                            letterSpacing = 0.3.sp
                        )
                        Text(
                            "Sign in to continue",
                            fontSize  = 14.sp,
                            color     = Color(0xFF7E8CB0),
                            modifier  = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── CAMPO EMAIL ───────────────────────────────────────────────
                ValidatedLoginField(
                    label           = "Email",
                    value           = viewModel.email,
                    onValueChange   = { viewModel.onEmailChanged(it) },
                    placeholder     = "your@email.com",
                    leadingIcon     = {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = null,
                            tint = if (isEmailValid) Color(0xFF1565C0) else Color(0xFFEF5350),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isError         = !isEmailValid,
                    errorMessage    = "Enter a valid email address",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction    = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── CAMPO CONTRASEÑA ──────────────────────────────────────────
                ValidatedLoginField(
                    label         = "Password",
                    value         = viewModel.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    placeholder   = "Min. 4 characters",
                    leadingIcon   = {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            tint = if (isPasswordValid) Color(0xFF1565C0) else Color(0xFFEF5350),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isPassword    = true,
                    passwordVisible    = viewModel.passwordVisible,
                    onVisibilityChange = { viewModel.togglePasswordVisibility() },
                    isError       = !isPasswordValid,
                    errorMessage  = "Password must be at least 4 characters",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction    = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                // ── FORGOT PASSWORD ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        "Forgot your password?",
                        color          = Color(0xFF1565C0),
                        fontSize       = 13.sp,
                        fontWeight     = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline,
                        modifier       = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── CHECKBOX TÉRMINOS ─────────────────────────────────────────
                val checkboxColor by animateColorAsState(
                    targetValue = if (viewModel.acceptedTerms) Color(0xFF1565C0) else Color(0xFFB0BEC5),
                    label = "checkboxColor"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8ECF8))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Checkbox(
                        checked         = viewModel.acceptedTerms,
                        onCheckedChange = { viewModel.onTermsChanged(it) },
                        colors          = CheckboxDefaults.colors(
                            checkedColor   = checkboxColor,
                            uncheckedColor = Color(0xFFB0BEC5)
                        )
                    )
                    Text("I accept the ", fontSize = 13.sp, color = Color(0xFF4A5568))
                    Text(
                        "Terms & Conditions",
                        fontSize       = 13.sp,
                        color          = Color(0xFF1565C0),
                        fontWeight     = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier       = Modifier.clickable { }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // ── ERROR DEL BACKEND ─────────────────────────────────────────
                AnimatedVisibility(
                    visible = viewModel.errorMessage != null,
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text     = "❌ ${viewModel.errorMessage}",
                        fontSize = 12.sp,
                        color    = Color(0xFFEF5350),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // ── BOTÓN LOGIN ───────────────────────────────────────────────
                Button(
                    onClick = {
                        if (canLogin) {
                            btnPressed = true
                            viewModel.onLoginClick { username, isAdmin ->
                                onLoginSuccess(username, isAdmin)
                            }
                        } else {
                            loginError = true
                        }
                    },
                    enabled  = !viewModel.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(btnScale)
                        .offset(x = shakeOffset.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (canLogin) 8.dp else 0.dp
                    )
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color    = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "SIGN IN",
                            fontSize      = 16.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            color         = Color.White,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // ── MENSAJE DE VALIDACIÓN GLOBAL ──────────────────────────────
                AnimatedVisibility(
                    visible = !canLogin && (viewModel.email.isNotEmpty() || viewModel.password.isNotEmpty()),
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    val hint = when {
                        !isEmailValid            -> "✉ Check your email format"
                        !isPasswordValid         -> "🔒 Password too short"
                        !viewModel.acceptedTerms -> "☑ Please accept the terms"
                        else                     -> "Fill in all fields to continue"
                    }
                    Text(
                        text     = hint,
                        fontSize = 12.sp,
                        color    = Color(0xFFEF5350),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ── Campo de texto con validación visual ──────────────────────────────────────
@Composable
fun ValidatedLoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityChange: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isError            -> Color(0xFFFFEBEE)
            value.isNotEmpty() -> Color(0xFFE8F0FE)
            else               -> Color(0xFFF1F3F8)
        },
        animationSpec = tween(300),
        label = "fieldBg"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontWeight    = FontWeight.SemiBold,
            color         = Color(0xFF0D1B3E),
            fontSize      = 13.sp,
            letterSpacing = 0.4.sp,
            modifier      = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value           = value,
            onValueChange   = onValueChange,
            placeholder     = { Text(placeholder, color = Color(0xFFADB5BD), fontSize = 14.sp) },
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(14.dp),
            singleLine      = true,
            leadingIcon     = leadingIcon,
            isError         = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { onVisibilityChange?.invoke() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = Color(0xFF7E8CB0),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = bgColor,
                unfocusedContainerColor = bgColor,
                errorContainerColor     = bgColor,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor     = Color.Transparent,
                focusedTextColor        = Color(0xFF0D1B3E),
                unfocusedTextColor      = Color(0xFF0D1B3E)
            )
        )

        AnimatedVisibility(
            visible = isError && value.isNotEmpty(),
            enter   = fadeIn(tween(200)) + expandVertically(),
            exit    = fadeOut(tween(200)) + shrinkVertically()
        ) {
            Text(
                text     = "⚠ $errorMessage",
                fontSize = 11.sp,
                color    = Color(0xFFEF5350),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}