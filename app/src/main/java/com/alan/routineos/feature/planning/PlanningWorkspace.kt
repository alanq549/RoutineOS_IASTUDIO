package com.alan.routineos.feature.planning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alan.routineos.core.designsystem.component.RoutineScaffold
import com.alan.routineos.core.designsystem.theme.RoutineTheme

@Composable
fun PlanningWorkspace(
    onAddActivity: () -> Unit,
    onActivityClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToAccount: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {}
) {
    RoutineScaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                // Main Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = RoutineTheme.spacing.marginMobile)
                        .padding(top = RoutineTheme.spacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Planificar",
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

                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        bottomBar = bottomBar
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PlanningRoute(
                onNavigateToActivityCreation = onAddActivity,
                onNavigateToActivityDetail = onActivityClick
            )
        }
    }
}
