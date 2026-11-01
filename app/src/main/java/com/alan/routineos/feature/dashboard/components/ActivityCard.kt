package com.alan.routineos.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.component.RoutineCard
import com.alan.routineos.core.designsystem.component.RoutineDropdownMenu
import com.alan.routineos.core.designsystem.component.RoutineDropdownMenuItem
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.dashboard.model.ActivityCardModel

/**
 * RoutineCard fielmente alineada con la especificación Stitch
 * (Obsidian Technical Architecture).
 * Superficie surface-container-low (#181c23), esquinas rounded-xl (12dp),
 * header con icono 40dp en surface-container (#1c2027), punto de subsistema,
 * matriz de telemetría y árbol de circuitos interactivo.
 */
@Composable
fun ActivityCard(
    activity: ActivityCardModel,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSheetMode: Boolean = false
) {
    val semanticColor = try { 
        Color(android.graphics.Color.parseColor(activity.iconColorHex)) 
    } catch (e: Exception) { 
        RoutineTheme.colors.primary 
    }
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = RoutineTheme.colors.surface1, // surface-container-low #181c23
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CARD HEADER: Icono 40dp + (Dot + SUBSISTEMA) + Título + Subtítulo | Cadencia + ⋮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Recuadro del icono: w-10 h-10 rounded-xl bg-surface-container
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(RoutineTheme.colors.surface2, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTechnicalIcon(activity.iconName),
                            contentDescription = null,
                            tint = semanticColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        // Dot de color de subsistema + Nombre en JetBrains Mono
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(semanticColor, CircleShape)
                            )
                            Text(
                                text = (activity.systemTitle ?: "RUTINA").uppercase(),
                                style = RoutineTheme.typography.codeSm.copy(
                                    fontSize = 10.sp,
                                    color = semanticColor,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = activity.title,
                            style = RoutineTheme.typography.headlineSmall.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = RoutineTheme.colors.onSurface
                        )

                        if (activity.subtitle.isNotBlank()) {
                            Text(
                                text = activity.subtitle,
                                style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp),
                                color = RoutineTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = activity.frequency.uppercase(),
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 10.sp,
                                color = RoutineTheme.colors.onSurfaceVariant
                            )
                        )
                        Text(
                            text = activity.durationText,
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = RoutineTheme.colors.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = RoutineTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        RoutineDropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            RoutineDropdownMenuItem(
                                text = "Editar rutina",
                                onClick = {
                                    showMenu = false
                                    (onEditClick ?: onClick).invoke()
                                },
                                icon = Icons.Default.Edit
                            )
                            if (onDeleteClick != null) {
                                RoutineDropdownMenuItem(
                                    text = "Eliminar rutina",
                                    onClick = {
                                        showMenu = false
                                        onDeleteClick()
                                    },
                                    icon = Icons.Default.Delete,
                                    iconColor = RoutineTheme.colors.error
                                )
                            }
                        }
                    }
                }
            }

            // TELEMETRÍA METADATA MATRIX: CADENCIA | SESIONES | ESTRUCTURA
            val segments = activity.statsLine.split("·").map { it.trim() }.filter { it.isNotBlank() }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoutineTheme.colors.surfaceContainerLowest, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CADENCIA",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                    )
                    Text(
                        text = activity.frequency.uppercase(),
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoutineTheme.colors.onSurface
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SESIONES",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                    )
                    Text(
                        text = segments.firstOrNull() ?: activity.durationText,
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoutineTheme.colors.primary
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ESTRUCTURA",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                    )
                    Text(
                        text = if (segments.size > 1) segments[1].uppercase() else "PASOS",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoutineTheme.colors.onSurface
                        )
                    )
                }
            }

            // PLAN DE EJECUCIÓN: CIRCUITO EN ÁRBOL (Guía continua + rama + punto brillante)
            if (activity.summaryItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoutineTheme.colors.surfaceContainerLowest, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activity.summaryItems.forEach { summary ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Cabecera del día: icono schedule + día en verde
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = RoutineTheme.colors.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = summary.dayName.uppercase(),
                                    style = RoutineTheme.typography.codeSm.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoutineTheme.colors.primary
                                    )
                                )
                            }

                            // Pasos con circuito visual de guía continua
                            Column(
                                modifier = Modifier.padding(start = 2.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                summary.activities.forEach { activityTitle ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Canvas(modifier = Modifier.size(width = 16.dp, height = 20.dp)) {
                                            // Línea vertical continua de guía
                                            drawLine(
                                                color = Color(0xFF3C4A42),
                                                start = Offset(x = 4.dp.toPx(), y = 0f),
                                                end = Offset(x = 4.dp.toPx(), y = size.height),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                            // Rama horizontal
                                            drawLine(
                                                color = Color(0xFF3C4A42),
                                                start = Offset(x = 4.dp.toPx(), y = size.height / 2),
                                                end = Offset(x = 10.dp.toPx(), y = size.height / 2),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                            // Punto circular brillante del color del subsistema
                                            drawCircle(
                                                color = semanticColor.copy(alpha = 0.85f),
                                                radius = 2.5.dp.toPx(),
                                                center = Offset(x = 13.dp.toPx(), y = size.height / 2)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = activityTitle,
                                            style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp),
                                            color = RoutineTheme.colors.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (activity.moreDaysCount > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+ ${activity.moreDaysCount} DÍAS CONFIGURADOS",
                                style = RoutineTheme.typography.codeSm.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    color = RoutineTheme.colors.onSurfaceVariant
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = null,
                                tint = RoutineTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // PIE DE ACCIONES: [ + AÑADIR AL DÍA ] en verde esmeralda Stitch + "Pasos y reglas →"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutineTheme.colors.primary,
                        contentColor = RoutineTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSheetMode) "AÑADIR AL DÍA" else "ABRIR INGENIERÍA",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            letterSpacing = 0.5.sp
                        ),
                        color = RoutineTheme.colors.onPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable(onClick = onClick)
                ) {
                    Text(
                        text = "Pasos y reglas",
                        style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp),
                        color = RoutineTheme.colors.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = RoutineTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

fun getTechnicalIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "fitness_center" -> Icons.Default.FitnessCenter
        "school" -> Icons.Default.School
        "work" -> Icons.Default.Work
        "favorite" -> Icons.Default.Favorite
        "bedtime" -> Icons.Default.Bedtime
        "rocket" -> Icons.Default.RocketLaunch
        "account_tree" -> Icons.Default.AccountTree
        "science" -> Icons.Default.Science
        "psychology" -> Icons.Default.Psychology
        "palette" -> Icons.Default.Palette
        "layers" -> Icons.Default.Layers
        else -> Icons.Default.AccountTree
    }
}
