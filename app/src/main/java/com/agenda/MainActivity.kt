package com.agenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.agenda.data.database.AppDatabase
import com.agenda.ui.navigation.BottomNavigationBar
import com.agenda.ui.navigation.NavigationController
import com.agenda.ui.theme.AgendaAutoTheme
import com.agenda.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {

    private val database by lazy { AppDatabase.getDatabase(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val studentDao = database.studentDao()
            val studentViewModel: StudentViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return StudentViewModel(application, studentDao) as T
                    }
                }
            )

            val isDarkModeOverride by studentViewModel.isDarkMode.collectAsState()
            val useDarkTheme = isDarkModeOverride ?: isSystemInDarkTheme()

            AgendaAutoTheme(darkTheme = useDarkTheme) {
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { innerPadding ->
                    NavigationController(
                        navController = navController,
                        viewModel = studentViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}