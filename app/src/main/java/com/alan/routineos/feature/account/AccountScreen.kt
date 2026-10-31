package com.alan.routineos.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.component.RoutineScaffold
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.account.components.AccountProfileCard
import com.alan.routineos.feature.account.components.AccountSection

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

@Composable
fun AccountScreen(
    uiState: AccountUiState,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {}
) {
    RoutineScaffold(
        modifier = modifier,
        topBar = {
            AccountHeader(onBack = onBack)
        },
        bottomBar = bottomBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = RoutineTheme.spacing.marginMobile)
                    .padding(top = RoutineTheme.spacing.md, bottom = RoutineTheme.spacing.xl)
            ) {
                uiState.profile?.let {
                    AccountProfileCard(profile = it)
                }

                Spacer(modifier = Modifier.height(RoutineTheme.spacing.xl))

                uiState.sections.forEach { section ->
                    AccountSection(section = section)
                }

                Spacer(modifier = Modifier.height(RoutineTheme.spacing.lg))

                // Logout Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            RoutineTheme.colors.error.copy(alpha = 0.3f),
                            RoutineTheme.shapes.medium
                        )
                        .clickable(onClick = onLogout)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = RoutineTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                        color = RoutineTheme.colors.error,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Version Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RoutineOS · Versión ${uiState.version}",
                        style = RoutineTheme.typography.dataLarge.copy(fontSize = 12.sp),
                        color = RoutineTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountHeader(
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(RoutineTheme.colors.surface2, CircleShape)
                            .border(1.dp, RoutineTheme.colors.border, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = RoutineTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Column {
                Text(
                    text = "PERFIL Y AJUSTES",
                    style = RoutineTheme.typography.labelCaps.copy(letterSpacing = 2.sp),
                    color = RoutineTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cuenta",
                    style = RoutineTheme.typography.displayLarge.copy(fontSize = 32.sp),
                    color = RoutineTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
