package com.alan.routineos.feature.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme

@Composable
fun ObsidianLoginScreen(
    onSignInWithGoogle: () -> Unit,
    onEmailLogin: (email: String, password: String) -> Unit,
    onEmailRegister: (name: String, email: String, password: String) -> Unit,
    isLoading: Boolean = false,
    serverError: String? = null,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val displayError = localError ?: serverError

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RoutineTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = RoutineTheme.colors.surface1,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(RoutineTheme.colors.primary.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = RoutineTheme.colors.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "ROUTINEOS // ACCESO",
                    style = RoutineTheme.typography.codeSm.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = RoutineTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRegisterMode) "Crear cuenta" else "Iniciar sesión",
                    style = RoutineTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = RoutineTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mode Switcher: Iniciar Sesión | Registrarse
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoutineTheme.colors.surfaceContainerLowest, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(false to "INICIAR SESIÓN", true to "REGISTRARSE").forEach { (register, label) ->
                        val selected = isRegisterMode == register
                        Surface(
                            onClick = {
                                if (!isLoading) {
                                    isRegisterMode = register
                                    localError = null
                                }
                            },
                            color = if (selected) RoutineTheme.colors.surface2 else RoutineTheme.colors.surfaceContainerLowest,
                            shape = RoundedCornerShape(6.dp),
                            border = if (selected) BorderStroke(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.4f)) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = RoutineTheme.typography.codeSm.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = if (selected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; localError = null },
                        label = { Text("Nombre completo", fontSize = 12.sp) },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; localError = null },
                    label = { Text("Correo electrónico", fontSize = 12.sp) },
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; localError = null },
                    label = { Text("Contraseña", fontSize = 12.sp) },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (displayError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = RoutineTheme.colors.error.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, RoutineTheme.colors.error.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = displayError,
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp),
                            color = RoutineTheme.colors.error,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        when {
                            isRegisterMode && name.isBlank() -> localError = "Ingresa tu nombre completo"
                            email.isBlank() || !email.contains("@") -> localError = "Ingresa un correo válido"
                            password.length < 6 -> localError = "La contraseña debe tener al menos 6 caracteres"
                            isRegisterMode -> onEmailRegister(name.trim(), email.trim(), password)
                            else -> onEmailLogin(email.trim(), password)
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutineTheme.colors.primary,
                        contentColor = RoutineTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = RoutineTheme.colors.onPrimary
                        )
                    } else {
                        Text(
                            text = if (isRegisterMode) "CREAR CUENTA" else "INICIAR SESIÓN",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = RoutineTheme.colors.border.copy(alpha = 0.4f))
                    Text(
                        text = "O",
                        style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp),
                        color = RoutineTheme.colors.onSurfaceVariant
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = RoutineTheme.colors.border.copy(alpha = 0.4f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onSignInWithGoogle,
                    enabled = !isLoading,
                    border = BorderStroke(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = RoutineTheme.colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONTINUAR CON GOOGLE",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = RoutineTheme.colors.primary
                    )
                }
            }
        }
    }
}
