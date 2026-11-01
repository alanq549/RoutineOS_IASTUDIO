package com.alan.routineos.feature.account.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.account.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    profile: UserProfile,
    bio: String,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var currentBio by remember { mutableStateOf(bio) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = RoutineTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "EDITAR PERFIL",
                style = RoutineTheme.typography.codeSm.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = RoutineTheme.colors.primary
            )
            Text(
                text = "Identidad y Datos de Usuario",
                style = RoutineTheme.typography.headlineMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = RoutineTheme.colors.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo Nombre
            Text(
                text = "NOMBRE",
                style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoutineTheme.colors.primary,
                    unfocusedBorderColor = RoutineTheme.colors.border
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Email
            Text(
                text = "CORREO ELECTRÓNICO",
                style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoutineTheme.colors.primary,
                    unfocusedBorderColor = RoutineTheme.colors.border
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Bio / Rol
            Text(
                text = "ENFOQUE / BIOGRAFÍA",
                style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = currentBio,
                onValueChange = { currentBio = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoutineTheme.colors.primary,
                    unfocusedBorderColor = RoutineTheme.colors.border
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, RoutineTheme.colors.border)
                ) {
                    Text("Cancelar", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurfaceVariant)
                }

                Button(
                    onClick = { onSave(name, email, currentBio) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutineTheme.colors.primary,
                        contentColor = RoutineTheme.colors.onPrimary
                    )
                ) {
                    Text("Guardar", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun AppearanceDialog(
    currentAccentHex: String,
    onSelectAccent: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        Triple("Verde Esmeralda Neón", "#5AF0B3", Color(0xFF5AF0B3)),
        Triple("Índigo Eléctrico", "#818CF8", Color(0xFF818CF8)),
        Triple("Ámbar Técnico", "#FBBF24", Color(0xFFFBBF24))
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Apariencia y Acento Técnico",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Selecciona el acento cromático del protocolo Obsidian:",
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                    color = RoutineTheme.colors.onSurfaceVariant
                )
                options.forEach { (name, hex, color) ->
                    val isSelected = hex.equals(currentAccentHex, ignoreCase = true)
                    Surface(
                        color = if (isSelected) color.copy(alpha = 0.15f) else RoutineTheme.colors.surface2,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isSelected) color else RoutineTheme.colors.border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAccent(name, hex) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.size(16.dp).background(color, CircleShape))
                            Text(
                                text = name,
                                style = RoutineTheme.typography.codeSm.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) color else RoutineTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Listo", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.primary)
            }
        },
        containerColor = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    prefs: NotificationPreferences,
    onSave: (Boolean, Boolean, Int, Boolean, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var reminders by remember { mutableStateOf(prefs.reminderAlarms) }
    var advanceNotice by remember { mutableStateOf(prefs.routineAdvanceNotice) }
    var haptic by remember { mutableStateOf(prefs.hapticFeedback) }
    var morning by remember { mutableStateOf(prefs.morningBriefing) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = RoutineTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "NOTIFICACIONES & ALARMAS",
                style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = RoutineTheme.colors.primary
            )
            Text(
                text = "Protocolo de Entrega de Avisos",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )

            // Switch 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Alarmas de Recordatorios", style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp), color = RoutineTheme.colors.onSurface)
                    Text("Sonido a la hora exacta configurada", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp), color = RoutineTheme.colors.onSurfaceVariant)
                }
                Switch(checked = reminders, onCheckedChange = { reminders = it })
            }

            // Switch 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Aviso Anticipado (15 min antes)", style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp), color = RoutineTheme.colors.onSurface)
                    Text("Alerta para preparar el inicio del bloque de rutina", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp), color = RoutineTheme.colors.onSurfaceVariant)
                }
                Switch(checked = advanceNotice, onCheckedChange = { advanceNotice = it })
            }

            // Switch 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Respuesta Háptica / Vibración", style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp), color = RoutineTheme.colors.onSurface)
                    Text("Vibración en acciones de completado y alarmas", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp), color = RoutineTheme.colors.onSurfaceVariant)
                }
                Switch(checked = haptic, onCheckedChange = { haptic = it })
            }

            // Switch 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Resumen Matutino (08:00 AM)", style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp), color = RoutineTheme.colors.onSurface)
                    Text("Vista general de los bloques planeados para hoy", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp), color = RoutineTheme.colors.onSurfaceVariant)
                }
                Switch(checked = morning, onCheckedChange = { morning = it })
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onSave(reminders, advanceNotice, 15, haptic, morning) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoutineTheme.colors.primary,
                    contentColor = RoutineTheme.colors.onPrimary
                )
            ) {
                Text("Guardar Preferencias", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun DataManagementDialog(
    stats: DataStorageStats,
    onExport: () -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Gestión de Datos y Almacenamiento",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Telemetría de BD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoutineTheme.colors.surfaceContainerLowest, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("MOTOR", style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, color = RoutineTheme.colors.onSurfaceVariant))
                        Text(stats.databaseVersion, style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoutineTheme.colors.onSurface))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("TAMAÑO ESTIMADO", style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, color = RoutineTheme.colors.onSurfaceVariant))
                        Text(stats.storageSizeKb, style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoutineTheme.colors.primary))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rutinas configuradas:", style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp), color = RoutineTheme.colors.onSurfaceVariant)
                    Text("${stats.routinesCount}", style = RoutineTheme.typography.codeSm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurface)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Instancias y eventos:", style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp), color = RoutineTheme.colors.onSurfaceVariant)
                    Text("${stats.plannedEventsCount}", style = RoutineTheme.typography.codeSm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurface)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pendientes en Backlog:", style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp), color = RoutineTheme.colors.onSurfaceVariant)
                    Text("${stats.backlogCount}", style = RoutineTheme.typography.codeSm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurface)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onExport,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoutineTheme.colors.surface2, contentColor = RoutineTheme.colors.primary)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EXPORTAR RESPALDO JSON", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onClearHistory,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, RoutineTheme.colors.error.copy(alpha = 0.5f))
                ) {
                    Text("PURGAR HISTORIAL COMPLETADO", style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = RoutineTheme.colors.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurfaceVariant)
            }
        },
        containerColor = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Privacidad y Soberanía Local",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )
        },
        text = {
            Text(
                text = "RoutineOS opera con arquitectura Offline-First. Toda tu información de rutinas, registros horarios y pendientes se almacena localmente en la base de datos SQLite (Room) en tu dispositivo.\n\nLa integración con Google Calendar utiliza tokens de solo lectura directos mediante OAuth para proyectar eventos en tiempo real sin almacenar copias en servidores externos.",
                style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = RoutineTheme.colors.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.primary)
            }
        },
        containerColor = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun AboutDialog(version: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Acerca de RoutineOS",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "RoutineOS · Versión $version",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    color = RoutineTheme.colors.primary
                )
                Text(
                    text = "Sistema operativo de hábitos, planificación y ejecución horaria diseñado con el estándar Obsidian Technical Architecture.\n\nDesarrollado para optimizar el rendimiento cognitivo y eliminar la fricción entre la intención y la acción diaria.",
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = RoutineTheme.colors.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.primary)
            }
        },
        containerColor = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Cerrar sesión?",
                style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.onSurface
            )
        },
        text = {
            Text(
                text = "Tus datos locales seguirán a salvo en el dispositivo. Deberás reconectar tus integraciones la próxima vez que inicies sesión.",
                style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                color = RoutineTheme.colors.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = RoutineTheme.colors.error, contentColor = RoutineTheme.colors.onPrimary),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Cerrar sesión", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", style = RoutineTheme.typography.codeSm.copy(fontWeight = FontWeight.Bold), color = RoutineTheme.colors.onSurfaceVariant)
            }
        },
        containerColor = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp)
    )
}
