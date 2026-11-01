package com.alan.routineos.feature.planning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.domain.model.DailyInstanceStatus
import com.alan.routineos.feature.today.model.PlanningItemType
import com.alan.routineos.feature.today.model.TodayTimelineUiModel

/**
 * Card para ítems "flotantes" (sin hora fija): Recordatorio y Tarea.
 * Adopta el riel lateral de acento semántico de Stitch V3.
 */
@Composable
private fun FloatingItemCard(
    item: TodayTimelineUiModel,
    onAction: (String, String) -> Unit,
    icon: ImageVector,
    badgeText: String,
    modifier: Modifier = Modifier
) {
    val isOmitted = item.status == DailyInstanceStatus.OMITTED
    val isModified = item.status == DailyInstanceStatus.MODIFIED
    val semanticColor = when (item.itemType) {
        PlanningItemType.ACTIVITY -> RoutineTheme.colors.roleEvent
        PlanningItemType.TASK -> RoutineTheme.colors.roleTask
        PlanningItemType.REMINDER -> RoutineTheme.colors.roleReminder
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoutineTheme.shapes.medium)
            .background(RoutineTheme.colors.surface1)
            .border(1.dp, semanticColor.copy(alpha = 0.28f), RoutineTheme.shapes.medium)
            .clickable { onAction(item.id, "EDIT_SPONTANEOUS") }
    ) {
        // LEFT ACCENT GLOW RAIL
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            semanticColor.copy(alpha = 0.9f),
                            semanticColor.copy(alpha = 0.4f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(semanticColor.copy(alpha = 0.12f), RoutineTheme.shapes.small)
                    .border(1.dp, semanticColor.copy(alpha = 0.3f), RoutineTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = semanticColor)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(semanticColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        badgeText,
                        style = RoutineTheme.typography.labelCaps.copy(
                            fontSize = 9.sp,
                            color = semanticColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.title,
                    style = RoutineTheme.typography.bodyBase.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = RoutineTheme.colors.onSurface
                )
                if (item.context?.note?.content?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.context.note.content,
                        style = RoutineTheme.typography.bodyBase.copy(fontSize = 11.sp),
                        color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            ActionMenu(item, isOmitted, isModified) { onAction(item.id, it) }
        }
    }
}

@Composable
fun PlanningReminderCard(
    item: TodayTimelineUiModel,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingItemCard(item, onAction, Icons.Default.Notifications, "RECORDATORIO", modifier)
}

@Composable
fun PlanningFloatingTaskCard(
    item: TodayTimelineUiModel,
    onAction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingItemCard(item, onAction, Icons.Default.Check, "TAREA PINEADA", modifier)
}