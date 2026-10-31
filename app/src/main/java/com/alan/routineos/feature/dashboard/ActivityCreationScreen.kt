package com.alan.routineos.feature.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alan.routineos.core.designsystem.component.RoutineScaffold
import com.alan.routineos.core.designsystem.component.RoutineTopBar
import com.alan.routineos.core.designsystem.theme.RoutineTheme

@Composable
fun ActivityCreationRoute(
    onBack: () -> Unit,
    viewModel: ActivityCreationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onBack()
        }
    }

    ActivityCreationScreen(
        uiState = uiState,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onSystemSelected = viewModel::onSystemSelected,
        onSaveClick = viewModel::saveActivity,
        onBackClick = onBack
    )
}

@Composable
fun ActivityCreationScreen(
    uiState: ActivityCreationUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSystemSelected: (String?) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    RoutineScaffold(
        topBar = { ActivityCreationTopBar(onBackClick) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = RoutineTheme.spacing.md)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NUEVA RUTINA",
                    style = RoutineTheme.typography.labelCaps.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Black,
                        color = RoutineTheme.colors.primary.copy(alpha = 0.6f)
                    )
                )
            }
            Spacer(modifier = Modifier.height(40.dp))

            ActivityFormFields(
                uiState = uiState,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onSystemSelected = onSystemSelected,
                enabled = !uiState.isSaving
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            ActivitySaveButton(
                onClick = onSaveClick,
                isSaving = uiState.isSaving,
                enabled = uiState.title.isNotBlank()
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ActivityCreationTopBar(onBackClick: () -> Unit) {
    Box(modifier = Modifier.statusBarsPadding()) {
        RoutineTopBar(
            title = {
                Text(
                    text = "Nueva Rutina",
                    style = RoutineTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }
}

@Composable
fun ActivityFormFields(
    uiState: ActivityCreationUiState,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSystemSelected: (String?) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // 01 // SISTEMA
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "01 //",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoutineTheme.colors.primary.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ÁREA O CATEGORÍA",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                    color = RoutineTheme.colors.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dynamic System Grid (Technical Tiles)
            if (uiState.allSystems.isEmpty()) {
                Text(
                    "No hay sistemas configurados.",
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f))
                )
            } else {
                uiState.allSystems.chunked(2).forEach { rowSystems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowSystems.forEach { system ->
                            SystemTile(
                                title = system.title,
                                sub = system.description.ifBlank { "Área de enfoque" },
                                color = try { Color(android.graphics.Color.parseColor(system.colorHex)) } catch (e: Exception) { RoutineTheme.colors.primary },
                                iconKey = system.iconKey,
                                isSelected = uiState.selectedSystemId == system.id,
                                onClick = { onSystemSelected(system.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowSystems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // 02 // TÍTULO
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "02 //",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoutineTheme.colors.primary.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TÍTULO",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                    color = RoutineTheme.colors.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                placeholder = { Text("Nombre de la rutina...", fontSize = 16.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.4f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoutineTheme.shapes.medium)
                    .background(RoutineTheme.colors.surface1)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.35f), RoutineTheme.shapes.medium),
                singleLine = true,
                enabled = enabled,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = RoutineTheme.typography.bodyBase.copy(fontWeight = FontWeight.Bold, color = RoutineTheme.colors.onSurface)
            )
        }

        // 03 // PROPÓSITO
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "03 //",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoutineTheme.colors.primary.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PROPÓSITO",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                    color = RoutineTheme.colors.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                placeholder = { Text("Descripción o propósito...", fontSize = 14.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.3f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoutineTheme.shapes.medium)
                    .background(RoutineTheme.colors.surface1)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.35f), RoutineTheme.shapes.medium),
                minLines = 3,
                enabled = enabled,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = RoutineTheme.typography.bodyBase.copy(fontSize = 14.sp, color = RoutineTheme.colors.onSurface)
            )
        }
    }
}

@Composable
private fun SystemTile(
    title: String,
    sub: String,
    color: Color,
    iconKey: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) color.copy(alpha = 0.12f) else RoutineTheme.colors.surface1,
        shape = RoutineTheme.shapes.medium,
        border = BorderStroke(1.dp, if (isSelected) color else RoutineTheme.colors.border.copy(alpha = 0.35f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = com.alan.routineos.feature.dashboard.components.getTechnicalIcon(iconKey),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = RoutineTheme.typography.labelCaps.copy(fontSize = 12.sp), color = RoutineTheme.colors.onSurface)
            Text(sub, style = RoutineTheme.typography.labelCaps.copy(fontSize = 9.sp), color = color.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun ActivitySaveButton(
    onClick: () -> Unit,
    isSaving: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isSaving,
        shape = RoutineTheme.shapes.pill,
        colors = ButtonDefaults.buttonColors(
            containerColor = RoutineTheme.colors.primary,
            contentColor = RoutineTheme.colors.onPrimary
        )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = RoutineTheme.colors.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "GUARDAR RUTINA",
                style = RoutineTheme.typography.labelCaps.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ActivityCreationScreenPreview() {
    RoutineTheme {
        ActivityCreationScreen(
            uiState = ActivityCreationUiState(title = "Mi nueva actividad"),
            onTitleChanged = {},
            onDescriptionChanged = {},
            onSystemSelected = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
