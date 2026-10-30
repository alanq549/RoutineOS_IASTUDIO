package com.alan.routineos.feature.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.component.RoutineDropdownMenu
import com.alan.routineos.core.designsystem.component.RoutineDropdownMenuItem
import com.alan.routineos.core.designsystem.component.RoutineEmptyState
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.dashboard.components.ActivityCard
import com.alan.routineos.feature.dashboard.components.LifeSystemEditorSheet
import com.alan.routineos.feature.dashboard.model.ActivityCardModel

@Composable
fun ActivityCatalogScreen(
    uiState: ActivityCatalogUiState,
    onAddActivity: () -> Unit,
    onActivityClick: (String) -> Unit,
    onDeleteActivity: ((String) -> Unit)? = null,
    onSystemSelected: (String?) -> Unit,
    onAddSystem: () -> Unit,
    onEditSystem: (String) -> Unit,
    onUpdateSystemFields: (String, String, String) -> Unit,
    onSaveSystem: () -> Unit,
    onDeleteSystem: (String) -> Unit,
    onDismissSystemEditor: () -> Unit,
    modifier: Modifier = Modifier,
    isSheetMode: Boolean = false
) {
    var activityToDelete by remember { mutableStateOf<ActivityCardModel?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isSheetMode) 24.dp else RoutineTheme.spacing.xl)
        ) {
            if (!isSheetMode) {
                Spacer(modifier = Modifier.height(RoutineTheme.spacing.lg))
            } else {
                Spacer(modifier = Modifier.height(RoutineTheme.spacing.sm))
            }

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = RoutineTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSheetMode) "Mis Rutinas y Actividades" else "Mis Actividades",
                        style = RoutineTheme.typography.headlineMedium,
                        color = RoutineTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(RoutineTheme.spacing.sm))
                    Text(
                        text = "${uiState.myActivities.size}",
                        style = RoutineTheme.typography.dataMedium,
                        color = RoutineTheme.colors.primary,
                        modifier = Modifier
                            .background(RoutineTheme.colors.surface1, RoutineTheme.shapes.small)
                            .border(1.dp, RoutineTheme.colors.border, RoutineTheme.shapes.small)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onAddActivity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutineTheme.colors.primary,
                            contentColor = Color.Black
                        ),
                        shape = RoutineTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RUTINA",
                            style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                    TextButton(
                        onClick = onAddSystem,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(RoutineTheme.dimensions.iconXs))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("ÁREA", style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SYSTEM FILTER ROW
            LazyRow(
                contentPadding = PaddingValues(horizontal = RoutineTheme.spacing.md),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    SystemFilterChip(
                        name = "Todas",
                        count = uiState.totalActivitiesCount,
                        icon = Icons.Default.AccountTree,
                        color = RoutineTheme.colors.onSurfaceVariant,
                        isSelected = uiState.selectedSystemId == null,
                        onClick = { onSystemSelected(null) }
                    )
                }
                items(uiState.allSystems) { system ->
                    SystemFilterChip(
                        name = system.title,
                        count = uiState.systemCounts[system.id] ?: 0,
                        icon = getTechnicalIcon(system.iconKey),
                        color = Color(android.graphics.Color.parseColor(system.colorHex)),
                        isSelected = uiState.selectedSystemId == system.id,
                        onClick = { onSystemSelected(system.id) },
                        onEditClick = { onEditSystem(system.id) },
                        onDeleteClick = { onDeleteSystem(system.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(RoutineTheme.spacing.lg))

            Column(modifier = Modifier.padding(horizontal = RoutineTheme.spacing.md)) {
                if (uiState.myActivities.isEmpty()) {
                    val isFiltered = uiState.selectedSystemId != null
                    RoutineEmptyState(
                        title = if (isFiltered) "Sin actividades en esta área" else "Sin actividades registradas",
                        description = if (isFiltered) {
                            "No tienes rutinas asignadas a este sistema de vida."
                        } else {
                            "Crea tu primera actividad habitual para estructurar tus días."
                        },
                        icon = Icons.Default.AccountTree,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    uiState.myActivities.forEach { activity ->
                        ActivityCard(
                            activity = activity,
                            onClick = { onActivityClick(activity.id) },
                            onEditClick = { onActivityClick(activity.id) },
                            onDeleteClick = if (onDeleteActivity != null) { { activityToDelete = activity } } else null
                        )
                        Spacer(modifier = Modifier.height(RoutineTheme.spacing.md))
                    }
                }
            }
        }
        
        if (!isSheetMode) {
            FloatingActionButton(
                onClick = onAddActivity,
                containerColor = RoutineTheme.colors.primary,
                contentColor = RoutineTheme.colors.onPrimary,
                shape = RoutineTheme.shapes.medium,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(RoutineTheme.spacing.lg)
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add activity")
            }
        }

        if (activityToDelete != null) {
            val target = activityToDelete!!
            AlertDialog(
                onDismissRequest = { activityToDelete = null },
                title = {
                    Text(
                        text = "¿Eliminar rutina?",
                        style = RoutineTheme.typography.headlineMedium,
                        color = RoutineTheme.colors.onSurface
                    )
                },
                text = {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar la rutina \"${target.title}\"? Esta acción no se puede deshacer y se eliminarán sus pasos configurados.",
                        style = RoutineTheme.typography.bodyBase,
                        color = RoutineTheme.colors.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteActivity?.invoke(target.id)
                            activityToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutineTheme.colors.error,
                            contentColor = Color.White
                        ),
                        shape = RoutineTheme.shapes.small
                    ) {
                        Text("Eliminar", style = RoutineTheme.typography.labelCaps.copy(fontWeight = FontWeight.Bold))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activityToDelete = null }) {
                        Text("Cancelar", style = RoutineTheme.typography.labelCaps, color = RoutineTheme.colors.onSurfaceVariant)
                    }
                },
                containerColor = RoutineTheme.colors.surface1,
                shape = RoutineTheme.shapes.large
            )
        }

        if (uiState.editingSystem != null) {
            LifeSystemEditorSheet(
                system = uiState.editingSystem,
                isCreationMode = uiState.isCreatingNewSystem,
                onUpdateFields = onUpdateSystemFields,
                onSave = onSaveSystem,
                onDelete = onDeleteSystem,
                onDismiss = onDismissSystemEditor
            )
        }
    }
}

@Composable
private fun SystemFilterChip(
    name: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.12f) else RoutineTheme.colors.surface2
    val borderColor = if (isSelected) color.copy(alpha = 0.8f) else RoutineTheme.colors.border.copy(alpha = 0.4f)
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .height(44.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(
                start = 14.dp,
                end = if (isSelected && (onEditClick != null || onDeleteClick != null)) 6.dp else 14.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = if (isSelected) color.copy(alpha = 0.2f) else RoutineTheme.colors.background.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(width = 24.dp, height = 18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = count.toString(),
                        style = RoutineTheme.typography.dataLarge.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (isSelected) color else RoutineTheme.colors.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name.uppercase(),
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                color = if (isSelected) Color.White else RoutineTheme.colors.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )

            if (isSelected && (onEditClick != null || onDeleteClick != null)) {
                Spacer(modifier = Modifier.width(4.dp))
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones del sistema",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    RoutineDropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (onEditClick != null) {
                            RoutineDropdownMenuItem(
                                text = "Editar sistema",
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                },
                                icon = Icons.Default.Edit
                            )
                        }
                        if (onDeleteClick != null) {
                            RoutineDropdownMenuItem(
                                text = "Eliminar sistema",
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
    }
}

private fun getTechnicalIcon(name: String): ImageVector {
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
