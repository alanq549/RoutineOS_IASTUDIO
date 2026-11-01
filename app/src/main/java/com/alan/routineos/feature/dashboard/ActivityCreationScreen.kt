package com.alan.routineos.feature.dashboard

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header Protocol Eyebrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(RoutineTheme.colors.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUILDER PROTOCOL v2.4",
                        style = RoutineTheme.typography.labelCaps.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = RoutineTheme.colors.primary
                        )
                    )
                }
                Text(
                    text = "SYS_READY",
                    style = RoutineTheme.typography.codeSm.copy(
                        fontSize = 10.sp,
                        color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            ActivityFormFields(
                uiState = uiState,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onSystemSelected = onSystemSelected,
                enabled = !uiState.isSaving
            )
            
            Spacer(modifier = Modifier.height(36.dp))

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
                    style = RoutineTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
    var selectedDispatchMode by remember { mutableIntStateOf(0) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // 01 // SISTEMA
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "01 //",
                        style = RoutineTheme.typography.labelCaps.copy(
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Black, 
                            color = RoutineTheme.colors.primary.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SISTEMA / DOMINIO PRIMARIO",
                        style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = RoutineTheme.colors.onSurface
                    )
                }
                Text(
                    text = "SYS_ID: #00",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f))
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            
            if (uiState.allSystems.isEmpty()) {
                Text(
                    "No hay sistemas configurados.",
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f))
                )
            } else {
                uiState.allSystems.chunked(2).forEach { rowSystems ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // 02 // TÍTULO DE LA RUTINA
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "02 //",
                        style = RoutineTheme.typography.labelCaps.copy(
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Black, 
                            color = RoutineTheme.colors.primary.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TÍTULO DE LA RUTINA",
                        style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = RoutineTheme.colors.onSurface
                    )
                }
                Text(
                    text = "ACT-TMPL",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.primary)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                placeholder = { 
                    Text("Ej: Gimnasio Hipertrofia, Estudio Idiomas...", fontSize = 15.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.4f)) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = RoutineTheme.colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoutineTheme.shapes.medium)
                    .background(RoutineTheme.colors.surface1)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f), RoutineTheme.shapes.medium),
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

        // 03 // PROPÓSITO & FUNCIÓN SISTÉMICA
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "03 //",
                    style = RoutineTheme.typography.labelCaps.copy(
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Black, 
                        color = RoutineTheme.colors.primary.copy(alpha = 0.6f)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PROPÓSITO & FUNCIÓN SISTÉMICA",
                    style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                    color = RoutineTheme.colors.onSurface
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                placeholder = { 
                    Text("Describe qué objetivo cumple esta rutina dentro de tu sistema...", fontSize = 13.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.35f)) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoutineTheme.shapes.medium)
                    .background(RoutineTheme.colors.surface1)
                    .border(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f), RoutineTheme.shapes.medium),
                minLines = 3,
                enabled = enabled,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                textStyle = RoutineTheme.typography.bodyBase.copy(fontSize = 13.sp, color = RoutineTheme.colors.onSurface)
            )
        }

        // VISUAL GRAPHIC ANCHOR: PIPELINE FLOW PREVIEW (Stitch V3)
        PipelineFlowPreview()

        // 04 // MECANISMO DE DESPACHO TEMPORAL
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "04 //",
                        style = RoutineTheme.typography.labelCaps.copy(
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Black, 
                            color = RoutineTheme.colors.primary.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MECANISMO DE DESPACHO TEMPORAL",
                        style = RoutineTheme.typography.labelCaps.copy(fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = RoutineTheme.colors.onSurface
                    )
                }
                Text(
                    text = "DISPATCH_MODE",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f))
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DispatchStrategyRow(
                    title = "Sin Horario Rígido (Ventana Flexible)",
                    description = "El algoritmo encaja el bloque según huecos libres del día.",
                    icon = Icons.Default.HourglassEmpty,
                    isSelected = selectedDispatchMode == 0,
                    onClick = { selectedDispatchMode = 0 }
                )
                DispatchStrategyRow(
                    title = "Hora Específica Fija",
                    description = "Ancla inamovible (Ej. 07:00 AM exacto en el cronograma).",
                    icon = Icons.Default.Alarm,
                    isSelected = selectedDispatchMode == 1,
                    onClick = { selectedDispatchMode = 1 }
                )
                DispatchStrategyRow(
                    title = "Hábito Encadenado",
                    description = "Se dispara inmediatamente tras completar otra actividad.",
                    icon = Icons.Default.Link,
                    isSelected = selectedDispatchMode == 2,
                    onClick = { selectedDispatchMode = 2 }
                )
            }
        }

        // INFORMATIVE SYSTEM DIRECTIVE BANNER
        Surface(
            color = RoutineTheme.colors.surface2.copy(alpha = 0.7f),
            shape = RoutineTheme.shapes.small,
            border = BorderStroke(1.dp, RoutineTheme.colors.border)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = RoutineTheme.colors.primary,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Al confirmar, el motor registrará esta rutina y desplegará el editor de pasos para pautar subtareas, descansos y reglas de repetición.",
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 12.sp, lineHeight = 17.sp),
                    color = RoutineTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PipelineFlowPreview() {
    Surface(
        color = RoutineTheme.colors.surface2,
        shape = RoutineTheme.shapes.medium,
        border = BorderStroke(1.dp, RoutineTheme.colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(RoutineTheme.colors.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PIPELINE DE EJECUCIÓN ESTIMADO",
                        style = RoutineTheme.typography.labelCaps.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoutineTheme.colors.onSurface
                        )
                    )
                }
                Text(
                    text = "~75 min",
                    style = RoutineTheme.typography.codeSm.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoutineTheme.colors.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Proportional Segments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(RoutineTheme.colors.secondary.copy(alpha = 0.8f))
                )
                Box(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .background(RoutineTheme.colors.primary)
                )
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .background(RoutineTheme.colors.tertiary.copy(alpha = 0.8f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "01. Activación",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f))
                )
                Text(
                    text = "02. Bloque Principal",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f))
                )
                Text(
                    text = "03. Descompresión",
                    style = RoutineTheme.typography.codeSm.copy(fontSize = 10.sp, color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f))
                )
            }
        }
    }
}

@Composable
private fun DispatchStrategyRow(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) RoutineTheme.colors.surface2 else RoutineTheme.colors.surface1,
        shape = RoutineTheme.shapes.small,
        border = BorderStroke(
            1.dp, 
            if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.5f) else RoutineTheme.colors.border
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.15f) else RoutineTheme.colors.surface2,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = RoutineTheme.typography.bodyBase.copy(
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    ),
                    color = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = RoutineTheme.typography.bodyBase.copy(fontSize = 11.sp, lineHeight = 15.sp),
                    color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        if (isSelected) RoutineTheme.colors.primary else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.border,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(RoutineTheme.colors.onPrimary, CircleShape)
                    )
                }
            }
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
        color = if (isSelected) color.copy(alpha = 0.14f) else RoutineTheme.colors.surface1,
        shape = RoutineTheme.shapes.small,
        border = BorderStroke(1.dp, if (isSelected) color else RoutineTheme.colors.border),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = com.alan.routineos.feature.dashboard.components.getTechnicalIcon(iconKey),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                title, 
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), 
                color = RoutineTheme.colors.onSurface
            )
            Text(
                sub, 
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 9.sp), 
                color = color.copy(alpha = 0.8f)
            )
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
            .height(50.dp),
        enabled = enabled && !isSaving,
        shape = RoutineTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = RoutineTheme.colors.primary,
            contentColor = RoutineTheme.colors.onPrimary
        )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = RoutineTheme.colors.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "CREAR Y CONFIGURAR PASOS",
                    style = RoutineTheme.typography.labelCaps.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                )
            }
        }
    }
}
