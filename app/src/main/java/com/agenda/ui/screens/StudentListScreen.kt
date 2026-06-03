package com.agenda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agenda.ui.components.student.AddStudent
import com.agenda.ui.components.student.OptionsBar
import com.agenda.ui.components.student.SearchBar
import com.agenda.ui.components.student.StudentCard
import com.agenda.viewmodel.FilterOption
import com.agenda.viewmodel.SortOption
import com.agenda.viewmodel.StudentViewModel

@Composable
fun StudentListScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.studentsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val currentSort by viewModel.sortOption.collectAsState()
    val currentFilter by viewModel.filterOption.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val sortLabel = when (currentSort) {
        SortOption.NAME_ASC -> "Nume (A-Z)"
        SortOption.NAME_DESC -> "Nume (Z-A)"
        SortOption.MONEY_DESC -> "Rest plată (Desc)"
        SortOption.MONEY_ASC -> "Rest plată (Cresc)"
        SortOption.LESSONS_DESC -> "Ore (Desc)"
        SortOption.LESSONS_ASC -> "Ore (Cresc)"
    }

    val filterLabel = when (currentFilter) {
        FilterOption.ALL -> "Toți"
        FilterOption.ACTIV -> "Activi"
        FilterOption.ADMIS -> "Admiși"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SearchBar(
            text = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OptionsBar(
                currentSortLabel = sortLabel,
                currentFilterLabel = filterLabel,
                onSortClick = { showSortMenu = true },
                onFilterClick = { showFilterMenu = true },
                onAddStudentClick = { showAddDialog = true }
            )

            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Nume (A-Z)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.NAME_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Nume (Z-A)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.NAME_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Rest plată (Descrescător)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.MONEY_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Rest plată (Crescător)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.MONEY_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ore efectuate (Descrescător)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.LESSONS_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ore efectuate (Crescător)") },
                    onClick = {
                        viewModel.setSortOption(SortOption.LESSONS_ASC)
                        showSortMenu = false
                    }
                )
            }

            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Toți Elevii") },
                    onClick = {
                        viewModel.setFilterOption(FilterOption.ALL)
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Doar Activi") },
                    onClick = {
                        viewModel.setFilterOption(FilterOption.ACTIV)
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Doar Admiși") },
                    onClick = {
                        viewModel.setFilterOption(FilterOption.ADMIS)
                        showFilterMenu = false
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(students) { currentStudent ->
                StudentCard(
                    student = currentStudent,
                    viewModel = viewModel,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }

    if (showAddDialog) {
        AddStudent(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false },
            onSave = { newStudent ->
                viewModel.insertStudent(newStudent)
                showAddDialog = false
            }
        )
    }
}