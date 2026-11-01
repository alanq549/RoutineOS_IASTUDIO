package com.alan.routineos.feature.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.stats.model.StatsPeriod

/**
 * Selector de horizonte temporal bajo el estándar Obsidian Technical Architecture.
 * Geometría semi-afilada (8px / 4px), tipografía JetBrains Mono y bordes nítidos de 1px.
 */
@Composable
fun StatsPeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Obsidian Segmented Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoutineTheme.shapes.small)
                .background(RoutineTheme.colors.surface1)
                .border(1.dp, RoutineTheme.colors.border, RoutineTheme.shapes.small)
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ObsidianPeriodButton(
                title = "SEMANA",
                isSelected = selectedPeriod == StatsPeriod.WEEK || selectedPeriod == StatsPeriod.DAY,
                onClick = { onPeriodSelected(StatsPeriod.WEEK) },
                modifier = Modifier.weight(1f)
            )
            ObsidianPeriodButton(
                title = "MES",
                isSelected = selectedPeriod == StatsPeriod.MONTH,
                onClick = { onPeriodSelected(StatsPeriod.MONTH) },
                modifier = Modifier.weight(1f)
            )
            ObsidianPeriodButton(
                title = "AÑO",
                isSelected = selectedPeriod == StatsPeriod.YEAR,
                onClick = { onPeriodSelected(StatsPeriod.YEAR) },
                modifier = Modifier.weight(1f)
            )
        }

        val rangeLabel = when (selectedPeriod) {
            StatsPeriod.MONTH -> "SYS_CYCLE // CICLO MENSUAL CONSOLIDADO"
            StatsPeriod.YEAR -> "SYS_HISTORY // HISTÓRICO ANUAL"
            else -> "SYS_RHYTHM // RITMO OPERATIVO SEMANAL"
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(RoutineTheme.colors.primary, CircleShape)
            )
            Text(
                text = rangeLabel,
                style = RoutineTheme.typography.codeSm.copy(
                    fontSize = 10.sp, 
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ObsidianPeriodButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBorder = RoutineTheme.colors.primary.copy(alpha = 0.4f)
    val inactiveBorder = Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) RoutineTheme.colors.surface2 else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) activeBorder else inactiveBorder,
                RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = RoutineTheme.typography.codeSm.copy(
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
        )
    }
}
