package com.alan.routineos.feature.stats.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.domain.model.SystemAdherence
import com.alan.routineos.domain.model.ActivityAdherence

import com.alan.routineos.core.designsystem.component.RoutineCard

@Composable
fun AdherenceList(
    title: String = "Adherencia por Sistema",
    systems: List<SystemAdherence> = emptyList(),
    activities: List<ActivityAdherence> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = RoutineTheme.typography.labelCaps.copy(letterSpacing = 1.5.sp),
            color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
        )

        if (systems.isEmpty() && activities.isEmpty()) {
            Text(
                "No hay actividad registrada en este periodo",
                style = RoutineTheme.typography.bodyBase,
                color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f)
            )
        } else {
            systems.forEach { system ->
                SystemAdherenceRow(system)
            }
            activities.forEach { activity ->
                ActivityAdherenceRow(activity)
            }
        }
    }
}

@Composable
private fun SystemAdherenceRow(system: SystemAdherence) {
    BaseAdherenceRow(
        name = system.systemName,
        completionRate = system.completionRate,
        omittedRate = system.omittedRate,
        missedRate = system.missedRate
    )
}

@Composable
private fun ActivityAdherenceRow(activity: ActivityAdherence) {
    BaseAdherenceRow(
        name = activity.activityTitle,
        completionRate = activity.completionRate,
        omittedRate = activity.omittedRate,
        missedRate = activity.missedRate,
        spontaneousCount = activity.spontaneousCount
    )
}

@Composable
private fun BaseAdherenceRow(
    name: String,
    completionRate: Float?,
    omittedRate: Float?,
    missedRate: Float?,
    spontaneousCount: Int = 0
) {
    RoutineCard(
        containerColor = RoutineTheme.colors.surface1,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = RoutineTheme.typography.bodyBase.copy(fontWeight = FontWeight.Bold),
                    color = RoutineTheme.colors.onSurface
                )
                Text(
                    text = completionRate?.let { "${(it * 100).toInt()}%" } ?: "N/A",
                    style = RoutineTheme.typography.dataLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    color = RoutineTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ratios bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoutineTheme.shapes.pill)
                    .background(RoutineTheme.colors.surface2)
            ) {
                val completed = completionRate ?: 0f
                val omitted = omittedRate ?: 0f
                val missed = missedRate ?: 0f
                
                val total = completed + omitted + missed
                if (total > 0f) {
                    if (completed > 0f) Box(modifier = Modifier.weight(completed).fillMaxHeight().background(RoutineTheme.colors.primary))
                    if (omitted > 0f) Box(modifier = Modifier.weight(omitted).fillMaxHeight().background(RoutineTheme.colors.surface3))
                    if (missed > 0f) Box(modifier = Modifier.weight(missed).fillMaxHeight().background(RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.2f)))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricLegend(label = "Completadas", color = RoutineTheme.colors.primary)
                MetricLegend(label = "Omitidas", color = RoutineTheme.colors.surface3)
                if (spontaneousCount > 0) {
                    MetricLegend(label = "Espontáneas ($spontaneousCount)", color = RoutineTheme.colors.tertiary)
                }
            }
        }
    }
}

@Composable
private fun MetricLegend(label: String, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoutineTheme.shapes.pill).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp),
            color = RoutineTheme.colors.onSurfaceVariant
        )
    }
}
