package com.alan.routineos.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
fun ActivityCard(
    activity: ActivityCardModel,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val semanticColor = try { Color(android.graphics.Color.parseColor(activity.iconColorHex)) } catch (e: Exception) { RoutineTheme.colors.primary }
    var showMenu by remember { mutableStateOf(false) }

    RoutineCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        containerColor = RoutineTheme.colors.surface1,
        border = BorderStroke(
            width = 1.dp,
            color = semanticColor.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(RoutineTheme.spacing.md)) {
            // TOP HEADER: Icon + Identity info + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(semanticColor.copy(alpha = 0.1f), RoutineTheme.shapes.small)
                            .border(1.dp, semanticColor.copy(alpha = 0.25f), RoutineTheme.shapes.small),
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
                        Text(
                            text = (activity.systemTitle ?: "SIN ÁREA").uppercase(),
                            style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp, color = semanticColor, fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = activity.title,
                            style = RoutineTheme.typography.headlineMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                            color = RoutineTheme.colors.onSurface
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = activity.frequency.uppercase(),
                            style = RoutineTheme.typography.labelCaps.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                            color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = activity.durationText,
                            style = RoutineTheme.typography.dataMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                            color = semanticColor
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = RoutineTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
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

            if (activity.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activity.subtitle,
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                    color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.75f),
                    modifier = Modifier.padding(start = 54.dp)
                )
            }

            if (activity.statsLine.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = activity.statsLine.uppercase(),
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp, letterSpacing = 0.5.sp),
                    color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 54.dp)
                )
            }

            // SUMMARY ITEMS (Clean presentation)
            if (activity.summaryItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoutineTheme.colors.surface2, RoutineTheme.shapes.small)
                        .border(1.dp, RoutineTheme.colors.border, RoutineTheme.shapes.small)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    activity.summaryItems.forEach { summary ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = summary.dayName.uppercase(),
                                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                    color = semanticColor
                                )
                                if (summary.detailText != null) {
                                    Text(
                                        text = summary.detailText,
                                        style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp),
                                        color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            summary.activities.forEach { activityName ->
                                Row(
                                    modifier = Modifier.padding(start = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(semanticColor.copy(alpha = 0.6f), CircleShape)
                                    )
                                    Text(
                                        text = activityName,
                                        style = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp),
                                        color = RoutineTheme.colors.onSurface.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    if (activity.moreDaysCount > 0) {
                        Text(
                            text = "+ ${activity.moreDaysCount} DÍAS MÁS",
                            style = RoutineTheme.typography.labelCaps.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tocar para organizar pasos y horarios",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp),
                    color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.45f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(14.dp)
                )
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
        else -> Icons.Default.AccountTree
    }
}
