package com.agenda.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person3
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed  class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home: Screen("home", "Home", Icons.Filled.Home)
    object Students: Screen("students", "Studenti", Icons.Filled.Person3)
    object Calendar: Screen("calendar", "Calendar", Icons.Filled.CalendarMonth)
    object Settings: Screen("settings", "Setari", Icons.Filled.Settings)
}