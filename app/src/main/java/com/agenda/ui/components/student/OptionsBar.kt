package com.agenda.ui.components.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionsBar(
    currentSortLabel: String = "Sortează",
    currentFilterLabel: String = "Filtrează",
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onAddStudentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = onSortClick,
                label = { Text(currentSortLabel) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.SortByAlpha,
                        contentDescription = "Sort Icon"
                    )
                }
            )

            AssistChip(
                onClick = onFilterClick,
                label = { Text(currentFilterLabel) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter Icon"
                    )
                }
            )
        }

        IconButton(
            onClick = onAddStudentClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adaugă student"
            )
        }
    }
}