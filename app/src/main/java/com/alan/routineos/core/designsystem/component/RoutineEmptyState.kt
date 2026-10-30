package com.alan.routineos.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alan.routineos.core.designsystem.theme.RoutineTheme

@Composable
fun RoutineEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(RoutineTheme.colors.surface2, RoutineTheme.shapes.medium)
                    .border(1.dp, RoutineTheme.colors.border, RoutineTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text(
            text = title,
            style = RoutineTheme.typography.bodyBase,
            color = RoutineTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (description != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp),
                color = RoutineTheme.colors.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}
