package com.alan.routineos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alan.routineos.core.designsystem.component.RoutineBottomBar
import com.alan.routineos.core.designsystem.theme.RoutineTheme
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.alan.routineos.core.navigation.AppNavHost
import com.alan.routineos.core.navigation.AppRoutes
import com.alan.routineos.data.calendar.GoogleCalendarSyncManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var calendarSyncManager: GoogleCalendarSyncManager

    @Inject
    lateinit var themePreferencesManager: com.alan.routineos.core.designsystem.theme.ThemePreferencesManager

    @Inject
    lateinit var authRepository: com.alan.routineos.data.auth.AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val accentColor by themePreferencesManager.accentColor.collectAsState()
            RoutineTheme(accentColor = accentColor) {
                val scope = rememberCoroutineScope()
                val permissionsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { results ->
                    if (results[Manifest.permission.READ_CALENDAR] == true) {
                        scope.launch {
                            calendarSyncManager.syncNow()
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    val toRequest = mutableListOf<String>()
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.READ_CALENDAR
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        toRequest.add(Manifest.permission.READ_CALENDAR)
                    } else {
                        calendarSyncManager.syncNow()
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        toRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (toRequest.isNotEmpty()) {
                        permissionsLauncher.launch(toRequest.toTypedArray())
                    }
                }

                val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val navigateTab: (String) -> Unit = { route ->
                        if (currentRoute == AppRoutes.Account.route) {
                            navController.popBackStack(AppRoutes.Account.route, inclusive = true)
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    val bottomBar: @Composable () -> Unit = {
                        RoutineBottomBar {
                            BottomNavItem(
                                label = "Today",
                                icon = Icons.Default.CalendarToday,
                                isSelected = currentRoute == AppRoutes.Today.route,
                                onClick = { navigateTab(AppRoutes.Today.route) }
                            )
                            BottomNavItem(
                                label = "Planning",
                                icon = Icons.Default.CalendarMonth,
                                isSelected = currentRoute == AppRoutes.Planning.route,
                                onClick = { navigateTab(AppRoutes.Planning.route) }
                            )
                            BottomNavItem(
                                label = "Stats",
                                icon = Icons.Default.Insights,
                                isSelected = currentRoute == AppRoutes.Stats.route,
                                onClick = { navigateTab(AppRoutes.Stats.route) }
                            )
                        }
                    }

                    AppNavHost(
                        navController = navController,
                        bottomBar = bottomBar
                    )
                }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) RoutineTheme.colors.primary else RoutineTheme.colors.onSurfaceVariant
    val background = if (isSelected) RoutineTheme.colors.primary.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent
    
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoutineTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .clip(RoutineTheme.shapes.small)
                .background(background)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
            Text(
                text = label,
                style = RoutineTheme.typography.labelCaps.copy(fontSize = 10.sp),
                color = color
            )
        }
    }
}
