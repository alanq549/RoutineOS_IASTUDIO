package com.alan.routineos.feature.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.alan.routineos.feature.dashboard.components.getTechnicalIcon
import com.alan.routineos.feature.dashboard.model.ActivityCardModel

/**
 * ActivityCatalogScreen alineada con la especificación Stitch
 * (Obsidian Technical Architecture).
 */
@Composable
fun ActivityCatalogScreen(
    uiState: ActivityCatalogUiState,
    onAddActivity: () -> Unit,
    onActivityClick: (String) -> Unit,
    onEditActivity: ((String) -> Unit)? = null,
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

    Box(modifier = modifier.fillMaxSize().background(RoutineTheme.colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = if (isSheetMode) 24.dp else RoutineTheme.spacing.xl)
        ) {
            if (isSheetMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(RoutineTheme.colors.border.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // HEADER SECTION WITH COUNTER & ACTIONS (Stitch Lines 21-36)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Rutinas",
                        style = RoutineTheme.typography.headlineMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = RoutineTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Badge contador: inline-flex rounded-full bg-surface-container-high text-primary
                    Box(
                        modifier = Modifier
                            .background(RoutineTheme.colors.surface3, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${uiState.myActivities.size}",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoutineTheme.colors.primary
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón + RUTINA: bg-primary text-on-primary
                    Button(
                        onClick = onAddActivity,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutineTheme.colors.primary,
                            contentColor = RoutineTheme.colors.onPrimary
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RUTINA",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Botón + ÁREA: bg-surface-container-high text-primary
                    Button(
                        onClick = onAddSystem,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutineTheme.colors.surface3,
                            contentColor = RoutineTheme.colors.primary
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = RoutineTheme.colors.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ÁREA", 
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoutineTheme.colors.primary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // HORIZONTAL FILTER CHIPS (Stitch Lines 38-63)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    SystemFilterChip(
                        name = "Todas",
                        count = uiState.totalActivitiesCount,
                        icon = Icons.Default.Layers,
                        isSelected = uiState.selectedSystemId == null,
                        onClick = { onSystemSelected(null) }
                    )
                }
                items(uiState.allSystems) { system ->
                    SystemFilterChip(
                        name = system.title,
                        count = uiState.systemCounts[system.id] ?: 0,
                        icon = getTechnicalIcon(system.iconKey),
                        isSelected = uiState.selectedSystemId == system.id,
                        onClick = { onSystemSelected(system.id) },
                        onEditClick = { onEditSystem(system.id) },
                        onDeleteClick = { onDeleteSystem(system.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ROUTINE CARDS STREAM (Stitch Lines 65-370)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                            onEditClick = { (onEditActivity ?: onActivityClick).invoke(activity.id) },
                            onDeleteClick = if (onDeleteActivity != null) { { activityToDelete = activity } } else null,
                            isSheetMode = isSheetMode
                        )
                    }
                }
            }
        }
        
        if (!isSheetMode) {
            FloatingActionButton(
                onClick = onAddActivity,
                containerColor = RoutineTheme.colors.primary,
                contentColor = RoutineTheme.colors.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
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
                            contentColor = RoutineTheme.colors.onPrimary
                        ),
                        shape = RoundedCornerShape(4.dp)
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
                shape = RoundedCornerShape(12.dp)
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

/**
 * SystemFilterChip idéntico a las clases de Stitch:
 * Active: rounded-xl bg-surface-container-highest text-primary
 * Inactive: rounded-xl bg-surface-container text-on-surface-variant
 */
@Composable
private fun SystemFilterChip(
    name: String,
    count: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val chipBackground = if (isSelected) RoutineTheme.colors.surfaceContainerHighest else RoutineTheme.colors.surface2
    val countBackground = if (isSelected) RoutineTheme.colors.surface2 else RoutineTheme.colors.surface3
    val contentColor = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
    val countTextColor = if (isSelected) RoutineTheme.colors.onSurface else RoutineTheme.colors.onSurfaceVariant
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = chipBackground,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height(32.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Count container: w-4 h-4 rounded
            Surface(
                color = countBackground,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$count",
                        style = RoutineTheme.typography.codeSm.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = countTextColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name.uppercase(),
                style = RoutineTheme.typography.codeSm.copy(
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor
            )

            if (isSelected && (onEditClick != null || onDeleteClick != null)) {
                Spacer(modifier = Modifier.width(4.dp))
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones del sistema",
                            tint = contentColor,
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
