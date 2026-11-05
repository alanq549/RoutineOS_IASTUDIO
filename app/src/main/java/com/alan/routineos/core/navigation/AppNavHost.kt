package com.alan.routineos.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alan.routineos.feature.account.AccountRoute
import com.alan.routineos.feature.dashboard.ActivityCreationRoute
import com.alan.routineos.feature.dashboard.ActivityDetailRoute
import com.alan.routineos.feature.planning.PlanningWorkspace
import com.alan.routineos.feature.stats.StatsRoute
import com.alan.routineos.feature.today.TodayRoute

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@Composable
fun AppNavHost(
    navController: NavHostController,
    bottomBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Today.route,
        enterTransition = {
            fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                slideInHorizontally(initialOffsetX = { it / 24 }, animationSpec = tween(200, easing = FastOutSlowInEasing))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(160, easing = FastOutSlowInEasing))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                slideInHorizontally(initialOffsetX = { -it / 24 }, animationSpec = tween(200, easing = FastOutSlowInEasing))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                slideOutHorizontally(targetOffsetX = { it / 24 }, animationSpec = tween(160, easing = FastOutSlowInEasing))
        },
        modifier = modifier
    ) {
        composable(AppRoutes.Today.route) {
            TodayRoute(
                bottomBar = bottomBar,
                onNavigateToAccount = { navController.navigate(AppRoutes.Account.route) }
            )
        }

        composable(AppRoutes.Planning.route) {
            PlanningWorkspace(
                bottomBar = bottomBar,
                onAddActivity = {
                    navController.navigate(AppRoutes.ActivityCreation.route)
                },
                onActivityClick = { activityId ->
                    navController.navigate(AppRoutes.ActivityDetail.createRoute(activityId))
                },
                onNavigateToAccount = { navController.navigate(AppRoutes.Account.route) }
            )
        }

        composable(AppRoutes.Stats.route) {
            StatsRoute(
                bottomBar = bottomBar,
                onNavigateToAccount = { navController.navigate(AppRoutes.Account.route) }
            )
        }
        composable(AppRoutes.Account.route) {
            AccountRoute(
                bottomBar = bottomBar,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.ActivityCreation.route) {
            ActivityCreationRoute(onBack = { navController.popBackStack() })
        }

        composable(
            route = AppRoutes.ActivityDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("activityId") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) {
            ActivityDetailRoute(onBack = { navController.popBackStack() })
        }
    }
}
