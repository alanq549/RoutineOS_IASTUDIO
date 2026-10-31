package com.alan.routineos.feature.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import com.alan.routineos.core.designsystem.component.RoutineCard
import com.alan.routineos.core.designsystem.component.RoutineEmptyState
import com.alan.routineos.core.designsystem.component.RoutineScaffold
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.stats.components.*
import com.alan.routineos.feature.stats.model.StatsPeriod

@Composable
fun StatsScreen(
    uiState: StatsUiState,
    onPeriodSelected: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAccount: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {}
) {
    RoutineScaffold(
        modifier = modifier,
        topBar = {
            StatsHeader(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = onPeriodSelected,
                onNavigateToAccount = onNavigateToAccount
            )
        },
        bottomBar = bottomBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RoutineTheme.colors.primary)
                }
            } else if (uiState.historySnapshot == null || uiState.historySnapshot.totalOccurrences == 0) {
                EmptyStatsView()
            } else {
                StatsScrollableContent(uiState)
            }
        }
    }
}

@Composable
private fun StatsHeader(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAccount: (() -> Unit)? = null
) {
    val borderColor = RoutineTheme.colors.border.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(RoutineTheme.colors.background)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .statusBarsPadding()
            .padding(horizontal = RoutineTheme.spacing.marginMobile)
            .padding(top = RoutineTheme.spacing.md, bottom = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "MÉTRICAS Y RENDIMIENTO",
                        style = RoutineTheme.typography.labelCaps.copy(letterSpacing = 2.sp),
                        color = RoutineTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estadísticas",
                        style = RoutineTheme.typography.displayLarge.copy(fontSize = 32.sp),
                        color = RoutineTheme.colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (onNavigateToAccount != null) {
                    IconButton(onClick = onNavigateToAccount) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(RoutineTheme.colors.surface2, CircleShape)
                                .border(1.dp, RoutineTheme.colors.border, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Cuenta",
                                tint = RoutineTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            StatsPeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatsScrollableContent(uiState: StatsUiState) {
    val snapshot = uiState.historySnapshot!!
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = RoutineTheme.spacing.marginMobile)
            .padding(top = RoutineTheme.spacing.md, bottom = RoutineTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // COMPACT HUD METRICS MATRIX (Stitch V3 Technical Standard)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HudMetricCard(
                label = "Cumplimiento",
                value = snapshot.completionRate?.let { "${(it * 100).toInt()}%" } ?: "0%",
                accent = RoutineTheme.colors.primary,
                modifier = Modifier.weight(1f)
            )
            val activeDaysCount = snapshot.dailyStats.count { it.completedCount > 0 }
            val totalDaysCount = snapshot.dailyStats.size.coerceAtLeast(1)
            HudMetricCard(
                label = "Días Activos",
                value = "$activeDaysCount de $totalDaysCount",
                accent = RoutineTheme.colors.primary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HudMetricCard(
                label = "Planificado",
                value = snapshot.plannedHours?.let { "${it.toInt()} h" } ?: "0 h",
                accent = RoutineTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
            HudMetricCard(
                label = "Ejecutado",
                value = snapshot.executedHours?.let { "${it.toInt()} h" } ?: "0 h",
                accent = RoutineTheme.colors.primary,
                modifier = Modifier.weight(1f)
            )
        }

        if (snapshot.deadlinesTotal > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RoutineTheme.colors.surface1, RoutineTheme.shapes.small)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.35f), RoutineTheme.shapes.small)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMPROMISOS A TIEMPO",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                    color = RoutineTheme.colors.onSurfaceVariant
                )
                Text(
                    text = "${snapshot.deadlinesMet} de ${snapshot.deadlinesTotal}",
                    style = RoutineTheme.typography.dataMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (snapshot.deadlinesMet == snapshot.deadlinesTotal) RoutineTheme.colors.primary else RoutineTheme.colors.warning
                )
            }
        }

        // PERIOD-SPECIFIC CONTENT (Focused & High-Density UX)
        AnimatedContent(
            targetState = uiState.selectedPeriod,
            transitionSpec = {
                fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
            },
            label = "StatsViewAnimation"
        ) { period ->
            when (period) {
                StatsPeriod.MONTH -> {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        snapshot.monthlyStats.firstOrNull()?.let { 
                            MonthlyCycleView(monthStats = it)
                        }
                        if (snapshot.systemAdherence.isNotEmpty()) {
                            AdherenceList(
                                title = "Adherencia por Área",
                                systems = snapshot.systemAdherence
                            )
                        }
                    }
                }
                StatsPeriod.YEAR -> {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        YearlyCycleView(year = "2026", months = snapshot.monthlyStats)
                        if (snapshot.systemAdherence.isNotEmpty()) {
                            AdherenceList(
                                title = "Adherencia por Área",
                                systems = snapshot.systemAdherence
                            )
                        }
                    }
                }
                else -> {
                    // Weekly Rhythm is the primary executive mode
                    WeeklyRhythmView(days = snapshot.dailyStats)
                }
            }
        }
    }
}

@Composable
private fun HudMetricCard(
    label: String, 
    value: String, 
    modifier: Modifier = Modifier,
    accent: Color = RoutineTheme.colors.primary
) {
    RoutineCard(
        containerColor = RoutineTheme.colors.surface1,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(RoutineTheme.shapes.pill)
                        .background(accent)
                )
                Text(
                    text = label.uppercase(),
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                    color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = RoutineTheme.typography.dataHero.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                color = accent
            )
        }
    }
}

@Composable
private fun EmptyStatsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        RoutineEmptyState(
            title = "Sin actividad registrada",
            description = "No hay ocurrencias registradas en este periodo",
            icon = Icons.Default.Insights
        )
    }
}
