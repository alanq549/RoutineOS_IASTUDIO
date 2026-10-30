package com.alan.routineos.feature.today

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TodayRoute(
    viewModel: TodayViewModel = hiltViewModel(),
    onNavigateToAccount: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    TodayScreen(
        uiState = uiState,
        onAction = viewModel::onActionTriggered,
        onExpandClick = viewModel::toggleExpand,
        onMetadataCaptured = viewModel::onMetadataCaptured,
        onCloseCapture = viewModel::onCloseCapture,
        onAddAdHoc = { title, start, end, deadline -> viewModel.onAddAdHoc(title, start, null, end, deadline) },
        onEditSpontaneous = viewModel::onEditSpontaneous,
        onDismissSpontaneousEditor = viewModel::onDismissSpontaneousEditor,
        onDeleteInstance = viewModel::onDeleteInstance,
        onUpdateInstanceTitle = viewModel::onUpdateInstanceTitle,
        onUpdateInstanceSchedule = viewModel::onUpdateInstanceSchedule,
        onConfirmPendingMove = viewModel::onConfirmPendingMove,
        onCancelPendingMove = viewModel::onCancelPendingMove,
        uiEvent = viewModel.uiEvent,
        onNavigateToAccount = onNavigateToAccount,
        bottomBar = bottomBar
    )
}
