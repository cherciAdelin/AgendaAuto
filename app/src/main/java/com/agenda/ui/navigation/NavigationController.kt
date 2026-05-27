package com.agenda.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.agenda.ui.screens.CalendarScreen
import com.agenda.ui.screens.HomeScreen
import com.agenda.ui.screens.SettingsScreen
import com.agenda.ui.screens.StudentListScreen
import com.agenda.viewmodel.StudentViewModel

@Composable
fun NavigationController(
    navController: NavHostController,
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,

        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
                    slideInVertically(
                        initialOffsetY = { 40 }, // Slides up slightly
                        animationSpec = tween(300)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
                    slideOutVertically(
                        targetOffsetY = { 40 }, // Slides down slightly
                        animationSpec = tween(300)
                    )
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel)
        }
        composable(Screen.Students.route) {
            StudentListScreen(viewModel = viewModel)
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}