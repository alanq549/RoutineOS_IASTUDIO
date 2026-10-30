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
        onLogout = viewModel::onLogout,
        onBack = onBack,
        bottomBar = bottomBar
    )
}
