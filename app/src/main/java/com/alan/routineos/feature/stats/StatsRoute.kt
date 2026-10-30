package com.alan.routineos.feature.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StatsRoute(
    viewModel: StatsViewModel = hiltViewModel(),
    onNavigateToAccount: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    StatsScreen(
        uiState = uiState,
        onPeriodSelected = viewModel::onPeriodSelected,
        onNavigateToAccount = onNavigateToAccount,
        bottomBar = bottomBar
    )
}
