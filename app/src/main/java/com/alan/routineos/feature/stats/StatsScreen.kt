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
        bottomBar = bottomBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Main Header
            Column(
                modifier = Modifier
                    .padding(horizontal = RoutineTheme.spacing.marginMobile)
                    .padding(top = RoutineTheme.spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Estadísticas",
                        style = RoutineTheme.typography.displayLarge,
                        color = RoutineTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )

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

                Spacer(modifier = Modifier.height(RoutineTheme.spacing.md))

                StatsPeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = onPeriodSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(RoutineTheme.spacing.md))

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
private fun StatsScrollableContent(uiState: StatsUiState) {
    val snapshot = uiState.historySnapshot!!
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = RoutineTheme.spacing.marginMobile)
            .padding(bottom = RoutineTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // GLOBAL SUMMARY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(RoutineTheme.spacing.md)
        ) {
            SummaryCard(
                label = "Adherencia Total",
                value = snapshot.completionRate?.let { "${(it * 100).toInt()}%" } ?: "0%",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Consistencia",
                value = snapshot.executionConsistency?.let { "${(it * 100).toInt()}%" } ?: "N/A",
                modifier = Modifier.weight(1f)
            )
        }

        // EXECUTION VS PLANNING SUMMARY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(RoutineTheme.spacing.md)
        ) {
            SummaryCard(
                label = "Planificado",
                value = snapshot.plannedHours?.let { "${it.toInt()} h" } ?: "0 h",
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                label = "Ejecutado",
                value = snapshot.executedHours?.let { "${it.toInt()} h" } ?: "0 h",
                modifier = Modifier.weight(1f)
            )
        }

        if (snapshot.deadlinesTotal > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(RoutineTheme.spacing.md)
            ) {
                SummaryCard(
                    label = "Compromisos a tiempo",
                    value = "${snapshot.deadlinesMet}/${snapshot.deadlinesTotal}",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = "Omitidas",
                    value = "${snapshot.omittedCount}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // GRANULAR VIEW (Animated Rhythm/Cycle)
        AnimatedContent(
            targetState = uiState.selectedPeriod,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "StatsViewAnimation"
        ) { period ->
            when (period) {
                StatsPeriod.DAY -> {
                    // In "Day" view, we show the daily breakdown directly without the weekly rhythm bar
                    snapshot.dailyStats.lastOrNull()?.let { 
                         DaySummaryContent(detail = it)
                    }
                }
                StatsPeriod.WEEK -> {
                    WeeklyRhythmView(days = snapshot.dailyStats)
                }
                StatsPeriod.MONTH -> {
                    snapshot.monthlyStats.firstOrNull()?.let { 
                         MonthlyCycleView(monthStats = it)
                    }
                }
                StatsPeriod.YEAR -> {
                    YearlyCycleView(year = "2026", months = snapshot.monthlyStats)
                }
            }
        }

        // ADHERENCE BREAKDOWN (Systems)
        AdherenceList(
            title = "Adherencia por Área",
            systems = snapshot.systemAdherence
        )

        // ADHERENCE BREAKDOWN (Activities - Universidad, Pull Day, etc.)
        if (snapshot.activityAdherence.isNotEmpty()) {
            AdherenceList(
                title = "Adherencia por Actividad",
                activities = snapshot.activityAdherence
            )
        }

        // FOCUS INDEX (N/A in v1)
        FocusPieChart()

        // TRENDS
        uiState.trendSeries.forEach { series ->
            MetadataLineChart(series = series)
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    RoutineCard(
        containerColor = RoutineTheme.colors.surface1,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label.uppercase(),
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = RoutineTheme.typography.dataLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = RoutineTheme.colors.primary
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
