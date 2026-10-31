package com.alan.routineos.feature.stats.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun StatsPeriodSelector(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RoutineTheme.colors.surface1, RoutineTheme.shapes.pill)
                .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f), RoutineTheme.shapes.pill)
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatsPeriodButton(
                title = "Semana",
                isSelected = selectedPeriod == StatsPeriod.WEEK || selectedPeriod == StatsPeriod.DAY,
                onClick = { onPeriodSelected(StatsPeriod.WEEK) },
                modifier = Modifier.weight(1f)
            )
            StatsPeriodButton(
                title = "Mes",
                isSelected = selectedPeriod == StatsPeriod.MONTH,
                onClick = { onPeriodSelected(StatsPeriod.MONTH) },
                modifier = Modifier.weight(1f)
            )
            StatsPeriodButton(
                title = "Año",
                isSelected = selectedPeriod == StatsPeriod.YEAR,
                onClick = { onPeriodSelected(StatsPeriod.YEAR) },
                modifier = Modifier.weight(1f)
            )
        }

        val rangeLabel = when (selectedPeriod) {
            StatsPeriod.MONTH -> "Ciclo Mensual · Visión Consolidada"
            StatsPeriod.YEAR -> "Ciclo Anual · Histórico"
            else -> "Ritmo Semanal · Flujo Operativo"
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoutineTheme.shapes.pill)
                    .background(RoutineTheme.colors.primary)
            )
            Text(
                text = rangeLabel.uppercase(),
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                color = RoutineTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatsPeriodButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoutineTheme.shapes.pill)
            .background(if (isSelected) RoutineTheme.colors.surface2 else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.35f) else Color.Transparent,
                RoutineTheme.shapes.pill
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = RoutineTheme.typography.labelCaps.copy(letterSpacing = 1.sp),
            color = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
