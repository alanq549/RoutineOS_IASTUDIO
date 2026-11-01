package com.alan.routineos.feature.account

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.component.RoutineScaffold
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.account.components.*
import com.alan.routineos.feature.account.model.AccountDialogType

@Composable
fun AccountScreen(
    uiState: AccountUiState,
    onEditProfileClick: () -> Unit,
    onSaveProfile: (String, String, String) -> Unit,
    onSettingClick: (String) -> Unit,
    onToggleCalendarProject: (Boolean) -> Unit,
    onToggleCalendarConflicts: (Boolean) -> Unit,
    onSyncCalendarNow: () -> Unit,
    onDisconnectCalendar: () -> Unit,
    onConnectCalendar: () -> Unit,
    onSelectAccent: (String, String) -> Unit,
    onSaveNotifications: (Boolean, Boolean, Int, Boolean, Boolean) -> Unit,
    onExportData: () -> Unit,
    onClearHistory: () -> Unit,
    onLogoutClick: () -> Unit,
    onConfirmLogout: () -> Unit,
    onDismissDialog: () -> Unit,
    onClearToast: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {}
) {
    // Auto dismiss toast after 2.5 seconds
    LaunchedEffect(uiState.toastMessage) {
        if (uiState.toastMessage != null) {
            kotlinx.coroutines.delay(2500)
            onClearToast()
        }
    }

    RoutineScaffold(
        modifier = modifier,
        topBar = {
            AccountHeader(onBack = onBack)
        },
        bottomBar = bottomBar
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de Perfil
                AccountProfileCard(
                    profile = uiState.profile,
                    onEditClick = onEditProfileClick
                )

                // Integración de Google Calendar
                Text(
                    text = "INTEGRACIONES & CALENDARIO",
                    style = RoutineTheme.typography.codeSm.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = RoutineTheme.colors.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                GoogleCalendarCard(
                    calendarState = uiState.calendarState,
                    onSyncNow = onSyncCalendarNow,
                    onToggleProject = onToggleCalendarProject,
                    onToggleConflicts = onToggleCalendarConflicts,
                    onDisconnect = onDisconnectCalendar,
                    onConnect = onConnectCalendar
                )

                // Secciones de Preferencias, Privacidad y Aplicación
                uiState.sections.forEach { section ->
                    AccountSection(
                        section = section,
                        onItemClick = onSettingClick
                    )
                }

                // Botón rojo Cerrar Sesión
                Surface(
                    color = RoutineTheme.colors.surface1,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, RoutineTheme.colors.error.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLogoutClick)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cerrar sesión",
                            style = RoutineTheme.typography.codeSm.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = RoutineTheme.colors.error
                        )
                    }
                }

                // Versión
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RoutineOS · Versión ${uiState.version}",
                        style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp),
                        color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            // Toast / Banner flotante de feedback
            AnimatedVisibility(
                visible = uiState.toastMessage != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Surface(
                    color = RoutineTheme.colors.surface3,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, RoutineTheme.colors.primary.copy(alpha = 0.5f)),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = RoutineTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = uiState.toastMessage ?: "",
                            style = RoutineTheme.typography.codeSm.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                            color = RoutineTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }

    // MODALES Y SHEETS FUNCIONALES
    when (uiState.activeDialog) {
        AccountDialogType.EDIT_PROFILE -> {
            EditProfileSheet(
                profile = uiState.profile,
                bio = uiState.profile.bio,
                onSave = onSaveProfile,
                onDismiss = onDismissDialog
            )
        }
        AccountDialogType.APPEARANCE -> {
            AppearanceDialog(
                currentAccentHex = uiState.appearancePrefs.accentHex,
                onSelectAccent = onSelectAccent,
                onDismiss = onDismissDialog
            )
        }
        AccountDialogType.NOTIFICATIONS -> {
            NotificationsSheet(
                prefs = uiState.notificationPrefs,
                onSave = onSaveNotifications,
                onDismiss = onDismissDialog
            )
        }
        AccountDialogType.DATA_MANAGEMENT -> {
            DataManagementDialog(
                stats = uiState.storageStats,
                onExport = onExportData,
                onClearHistory = onClearHistory,
                onDismiss = onDismissDialog
            )
        }
        AccountDialogType.PRIVACY -> {
            PrivacyDialog(onDismiss = onDismissDialog)
        }
        AccountDialogType.ABOUT -> {
            AboutDialog(version = uiState.version, onDismiss = onDismissDialog)
        }
        AccountDialogType.LOGOUT_CONFIRMATION -> {
            LogoutConfirmationDialog(
                onConfirm = onConfirmLogout,
                onDismiss = onDismissDialog
            )
        }
        else -> Unit
    }
}

@Composable
private fun AccountHeader(
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val borderColor = RoutineTheme.colors.border.copy(alpha = 0.4f)

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
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(RoutineTheme.colors.surface2, CircleShape)
                            .border(1.dp, RoutineTheme.colors.border, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = RoutineTheme.colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = "Cuenta",
                style = RoutineTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = RoutineTheme.colors.onSurface
            )
        }
    }
}
