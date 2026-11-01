package com.alan.routineos.feature.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AccountRoute(
    viewModel: AccountViewModel = viewModel(),
    onBack: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    AccountScreen(
        uiState = uiState,
        onEditProfileClick = viewModel::onEditProfileClick,
        onSaveProfile = viewModel::onSaveProfile,
        onSettingClick = viewModel::onSettingClick,
        onToggleCalendarProject = viewModel::onToggleCalendarProject,
        onToggleCalendarConflicts = viewModel::onToggleCalendarConflicts,
        onSyncCalendarNow = viewModel::onSyncCalendarNow,
        onDisconnectCalendar = viewModel::onDisconnectCalendar,
        onConnectCalendar = viewModel::onConnectCalendar,
        onSelectAccent = viewModel::onUpdateAppearance,
        onSaveNotifications = viewModel::onUpdateNotifications,
        onExportData = viewModel::onExportData,
        onClearHistory = viewModel::onClearHistory,
        onLogoutClick = viewModel::onLogoutClick,
        onConfirmLogout = viewModel::onConfirmLogout,
        onDismissDialog = viewModel::onDismissDialog,
        onClearToast = viewModel::onClearToast,
        onBack = onBack,
        bottomBar = bottomBar
    )
}
