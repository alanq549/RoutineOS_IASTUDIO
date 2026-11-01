@file:Suppress("NonObservableLocale")
package com.alan.routineos.feature.stats.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.component.RoutineCard
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.domain.model.DailyStats
import com.alan.routineos.domain.model.DailyInstanceStatus
import com.alan.routineos.domain.model.ResolvedOccurrence
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WeeklyRhythmView(
    days: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    var selectedDate by remember { mutableStateOf(days.lastOrNull()?.date) }
    val selectedDay = days.find { it.date == selectedDate } ?: days.lastOrNull()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Rhythm Chart Card (Obsidian Technical)
        RoutineCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(RoutineTheme.colors.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RITMO SEMANAL // TELEMETRÍA",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 10.sp, 
                                letterSpacing = 1.sp, 
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                    }

                    // Subtle Legend Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(RoutineTheme.colors.primary, CircleShape)
                            )
                            Text(
                                "Hecho",
                                style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp),
                                color = RoutineTheme.colors.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(RoutineTheme.colors.tertiary, CircleShape)
                            )
                            Text(
                                "Espontáneo",
                                style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp),
                                color = RoutineTheme.colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    days.forEach { day ->
                        RhythmDayColumn(
                            day = day,
                            isSelected = day.date == selectedDate,
                            onClick = { selectedDate = day.date },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Day Detail Section (Obsidian Detail Breakdown)
        selectedDay?.let { detail ->
            DaySummaryContent(detail = detail)
        }

        // Planificado vs Realizado Section (Obsidian Progress Bars)
        val activitySummaries = remember(days) {
            days.flatMap { it.occurrences }
                .groupBy { it.activityTitle ?: "Espontáneas" }
                .map { (title, occs) ->
                    val total = occs.size
                    val completed = occs.count { it.instance.status == DailyInstanceStatus.COMPLETED }
                    val rate = if (total > 0) completed.toFloat() / total else 0f
                    Triple(title, "$completed / $total", rate)
                }
                .filter { it.first != "Espontáneas" || it.second.isNotBlank() }
        }

        if (activitySummaries.isNotEmpty()) {
            RoutineCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PLANIFICADO VS REALIZADO",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 10.sp, 
                                letterSpacing = 1.sp, 
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoutineTheme.colors.onSurfaceVariant
                        )
                        Text(
                            text = "CUMPLIMIENTO",
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, color = RoutineTheme.colors.border)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        activitySummaries.forEach { (title, countText, rate) ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        style = RoutineTheme.typography.bodyBase.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp
                                        ),
                                        color = RoutineTheme.colors.onSurface
                                    )
                                    Text(
                                        text = countText,
                                        style = RoutineTheme.typography.dataMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        color = RoutineTheme.colors.primary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(RoutineTheme.colors.surface2)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = rate.coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(RoutineTheme.colors.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RhythmDayColumn(
    day: DailyStats,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEE", Locale("es", "ES"))
    
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.12f) else RoutineTheme.colors.surface2.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.6f) else RoutineTheme.colors.border.copy(alpha = 0.25f),
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Bottom)
        ) {
            // Stack segments
            if (day.missedCount > 0) {
                Box(
                    modifier = Modifier
                        .weight(day.missedCount.toFloat())
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(RoutineTheme.colors.onSurface.copy(alpha = 0.1f))
                )
            }
            if (day.omittedCount > 0) {
                Box(
                    modifier = Modifier
                        .weight(day.omittedCount.toFloat())
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(RoutineTheme.colors.surface3)
                )
            }
            if (day.spontaneousCount > 0) {
                Box(
                    modifier = Modifier
                        .weight(day.spontaneousCount.toFloat())
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(RoutineTheme.colors.tertiary)
                )
            }
            if (day.completedCount > 0) {
                Box(
                    modifier = Modifier
                        .weight(day.completedCount.toFloat())
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(RoutineTheme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    if (day.completedCount >= 2) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = RoutineTheme.colors.onPrimary,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
            }
            
            // If empty
            if (day.occurrences.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(RoutineTheme.colors.surface3)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day.date.format(dateFormatter).uppercase().replace(".", ""),
            style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
            color = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
        )
    }
}

@Composable
fun DaySummaryContent(detail: DailyStats, modifier: Modifier = Modifier) {
    val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
    
    RoutineCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = detail.date.format(fullDateFormatter).replaceFirstChar { it.uppercase() }, 
                        style = RoutineTheme.typography.headlineMedium.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold), 
                        color = RoutineTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${detail.occurrences.size} ocurrencias programadas", 
                        style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp), 
                        color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(RoutineTheme.colors.primary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .border(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = detail.completionRate?.let { "${(it * 100).toInt()}%" } ?: "0%", 
                        style = RoutineTheme.typography.dataMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), 
                        color = RoutineTheme.colors.primary
                    )
                }
            }

            if (detail.occurrences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                val groups = detail.occurrences.groupBy { it.activityTitle ?: "Espontáneas" }
                
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    groups.forEach { (title, occs) ->
                        ActivityGroupRow(title = title, occurrences = occs)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityGroupRow(title: String, occurrences: List<ResolvedOccurrence>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (title != "Espontáneas") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(RoutineTheme.colors.primary, CircleShape)
                )
                Text(
                    text = title.uppercase(),
                    style = RoutineTheme.typography.codeSm.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    ),
                    color = RoutineTheme.colors.primary
                )
            }
        }
        
        occurrences.forEach { occ ->
            val isCompleted = occ.instance.status == DailyInstanceStatus.COMPLETED
            val isOmitted = occ.instance.status == DailyInstanceStatus.OMITTED
            val isModified = occ.instance.status == DailyInstanceStatus.MODIFIED
            val statusText = when (occ.instance.status) {
                DailyInstanceStatus.COMPLETED -> "COMPLETADA"
                DailyInstanceStatus.OMITTED -> "OMITIDA"
                DailyInstanceStatus.MODIFIED -> "MODIFICADA"
                DailyInstanceStatus.PLANNED -> "PROGRAMADA"
            }

            val timeLabel = occ.instance.plannedStartTime?.let { minutes ->
                val h = minutes / 60
                val m = minutes % 60
                String.format(Locale.ROOT, "%02d:%02d", h, m)
            } ?: "--:--"

            // Contextual Accent color
            val accentColor = when {
                occ.isAdHoc -> RoutineTheme.colors.tertiary
                title.contains("Universidad", ignoreCase = true) || title.contains("Estudio", ignoreCase = true) -> RoutineTheme.colors.secondary
                title.contains("Salud", ignoreCase = true) || title.contains("Gimnasio", ignoreCase = true) -> RoutineTheme.colors.primary
                else -> RoutineTheme.colors.primary
            }

            // Obsidian Card with Left Accent Rail (3px)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(RoutineTheme.colors.surface2)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            ) {
                // LEFT ACCENT GLOW RAIL
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(accentColor)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                isCompleted -> Icons.Default.CheckCircle
                                isOmitted -> Icons.Default.Cancel
                                else -> Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = when {
                                isCompleted -> RoutineTheme.colors.primary
                                isOmitted -> RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f)
                                else -> RoutineTheme.colors.primary.copy(alpha = 0.7f)
                            },
                            modifier = Modifier.size(18.dp)
                        )

                        // Time badge in monospace JetBrains Mono
                        Box(
                            modifier = Modifier
                                .background(RoutineTheme.colors.surface1, RoundedCornerShape(4.dp))
                                .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = timeLabel,
                                style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp),
                                color = if (occ.instance.plannedStartTime != null) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
                            )
                        }

                        Column {
                            Text(
                                text = occ.instance.titleSnapshot,
                                style = RoutineTheme.typography.bodyBase.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    textDecoration = if (isOmitted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                ),
                                color = if (isOmitted) RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f) else RoutineTheme.colors.onSurface
                            )
                            if (occ.isAdHoc) {
                                Text(
                                    "ESPONTÁNEA", 
                                    style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = RoutineTheme.colors.tertiary
                                )
                            }
                        }
                    }

                    // Status Badge (Obsidian flat chip)
                    Box(
                        modifier = Modifier
                            .background(
                                when {
                                    isCompleted -> RoutineTheme.colors.primary.copy(alpha = 0.12f)
                                    isOmitted -> RoutineTheme.colors.surface3
                                    isModified -> RoutineTheme.colors.tertiary.copy(alpha = 0.12f)
                                    else -> RoutineTheme.colors.surface1
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                when {
                                    isCompleted -> RoutineTheme.colors.primary.copy(alpha = 0.3f)
                                    isModified -> RoutineTheme.colors.tertiary.copy(alpha = 0.3f)
                                    else -> RoutineTheme.colors.border.copy(alpha = 0.2f)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = when {
                                isCompleted -> RoutineTheme.colors.primary
                                isOmitted -> RoutineTheme.colors.onSurfaceVariant
                                isModified -> RoutineTheme.colors.tertiary
                                else -> RoutineTheme.colors.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}
