package com.alan.routineos.feature.account.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.account.model.GoogleCalendarState

@Composable
fun GoogleCalendarCard(
    calendarState: GoogleCalendarState,
    onSyncNow: () -> Unit,
    onToggleProject: (Boolean) -> Unit,
    onToggleConflicts: (Boolean) -> Unit,
    onDisconnect: () -> Unit,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = RoutineTheme.colors.surface1,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Icon + Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(RoutineTheme.colors.surface2, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Google Calendar",
                            tint = RoutineTheme.colors.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Google Calendar",
                            style = RoutineTheme.typography.headlineMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoutineTheme.colors.onSurface
                        )
                        Text(
                            text = calendarState.accountEmail,
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp),
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = if (calendarState.isConnected) RoutineTheme.colors.primary.copy(alpha = 0.12f) else RoutineTheme.colors.error.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (calendarState.isConnected) RoutineTheme.colors.primary.copy(alpha = 0.3f) else RoutineTheme.colors.error.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(
                                    if (calendarState.isConnected) RoutineTheme.colors.primary else RoutineTheme.colors.error,
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (calendarState.isConnected) "VINCULADO" else "DESCONECTADO",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = if (calendarState.isConnected) RoutineTheme.colors.primary else RoutineTheme.colors.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seamless Auto-Sync Status Bar (No manual action required)
            Surface(
                color = RoutineTheme.colors.surfaceContainerLowest,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (calendarState.isConnected) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(RoutineTheme.colors.primary, CircleShape)
                                )
                                Text(
                                    text = "AUTO-SYNC EN TIEMPO REAL",
                                    style = RoutineTheme.typography.codeSm.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = RoutineTheme.colors.primary
                                    )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(RoutineTheme.colors.error, CircleShape)
                                )
                                Text(
                                    text = "AUTO-SYNC PAUSADO",
                                    style = RoutineTheme.typography.codeSm.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = RoutineTheme.colors.error
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (calendarState.isConnected)
                                "Actualización continua en segundo plano · ${calendarState.syncedEventsCount} eventos"
                            else
                                "Conexión inactiva · Activa Google Calendar para reanudar",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 11.sp,
                                color = RoutineTheme.colors.onSurface
                            )
                        )

                        Text(
                            text = "Último refresco: ${calendarState.lastSyncTime}",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 9.sp,
                                color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    }

                    if (calendarState.isConnected) {
                        val infiniteTransition = rememberInfiniteTransition(label = "sync")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "rotation"
                        )

                        IconButton(
                            onClick = onSyncNow,
                            enabled = !calendarState.isSyncing,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Refrescar ahora",
                                tint = if (calendarState.isSyncing) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant,
                                modifier = Modifier
                                    .size(16.dp)
                                    .then(if (calendarState.isSyncing) Modifier.rotate(rotation) else Modifier)
                            )
                        }
                    }
                }
            }

            if (calendarState.isConnected) {
                Spacer(modifier = Modifier.height(14.dp))

                // Toggle 1: Proyectar franjas en Planning
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Proyectar eventos en Planning",
                            style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                            color = RoutineTheme.colors.onSurface
                        )
                        Text(
                            text = "Muestra franjas de reuniones externas en el cronograma semanal",
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp),
                            color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = calendarState.projectEventsInPlanning,
                        onCheckedChange = onToggleProject,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoutineTheme.colors.onPrimary,
                            checkedTrackColor = RoutineTheme.colors.primary,
                            uncheckedTrackColor = RoutineTheme.colors.surface2
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Toggle 2: Detección de solapamientos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Alertas de conflicto de horario",
                            style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                            color = RoutineTheme.colors.onSurface
                        )
                        Text(
                            text = "Avisa si una rutina choca con un compromiso de Google Calendar",
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp),
                            color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = calendarState.detectConflicts,
                        onCheckedChange = onToggleConflicts,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RoutineTheme.colors.onPrimary,
                            checkedTrackColor = RoutineTheme.colors.primary,
                            uncheckedTrackColor = RoutineTheme.colors.surface2
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDisconnect,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LinkOff,
                        contentDescription = null,
                        tint = RoutineTheme.colors.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Desvincular Google Calendar",
                        style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp),
                        color = RoutineTheme.colors.error
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutineTheme.colors.primary,
                        contentColor = RoutineTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "VINCULAR GOOGLE CALENDAR",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}
