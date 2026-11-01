package com.alan.routineos.feature.account.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alan.routineos.core.designsystem.component.RoutineSectionHeader
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import com.alan.routineos.feature.account.model.AccountSectionModel

@Composable
fun AccountSection(
    section: AccountSectionModel,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        RoutineSectionHeader(title = section.title)
        Surface(
            color = RoutineTheme.colors.surface1,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoutineTheme.colors.border.copy(alpha = 0.4f))
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    AccountSettingRow(
                        item = item,
                        onClick = { onItemClick(item.id) }
                    )
                    if (index < section.items.lastIndex) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(RoutineTheme.colors.border.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
