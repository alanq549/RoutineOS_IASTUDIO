package com.alan.routineos.feature.today.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.today.model.TodayProgress

@Composable
fun TodayHeader(
    dateText: String,
    progress: TodayProgress,
    modifier: Modifier = Modifier,
    onNavigateToAccount: (() -> Unit)? = null
) {
    val backgroundColor = RoutineTheme.colors.background
    val borderColor = RoutineTheme.colors.border.copy(alpha = 0.3f)
    
    // Header with a solid background and a matching soft bottom edge
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .drawBehind {
                // Technical hairline for crisp separation
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateText.uppercase(),
                style = RoutineTheme.typography.labelCaps.copy(letterSpacing = 2.sp),
                color = RoutineTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu Progreso",
                style = RoutineTheme.typography.displayLarge.copy(fontSize = 32.sp),
                color = RoutineTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            TodayProgressCircle(progress = progress)
        }

        if (onNavigateToAccount != null) {
            IconButton(
                onClick = onNavigateToAccount,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
            ) {
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
}
