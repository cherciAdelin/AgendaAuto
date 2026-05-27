package com.agenda.ui.components.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agenda.data.model.Student
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyDetails(
    date: LocalDate,
    lessons: List<Student>,
    exams: List<Student>,
    onDismiss: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ro", "RO")) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Program - ${date.format(dateFormatter)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (lessons.isEmpty() && exams.isEmpty()) {
                Text(
                    text = "Nicio programare pentru această zi.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (exams.isNotEmpty()) {
                Text(
                    text = "EXAMENE:",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                exams.sortedBy { it.upcomingExam }.forEach { student ->
                    ListItem(
                        headlineContent = { Text(student.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(student.phoneNumber ?: "Fără telefon") },
                        trailingContent = {
                            Text(
                                text = "Examen",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            if (lessons.isNotEmpty()) {
                Text(
                    text = "ȘEDINȚE:",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                lessons.sortedBy { it.upcomingLesson }.forEach { student ->
                    val time = student.upcomingLesson?.format(timeFormatter) ?: "--:--"
                    ListItem(
                        headlineContent = { Text(student.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(student.phoneNumber ?: "Fără telefon") },
                        trailingContent = {
                            Text(
                                text = time,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    )
                }
            }
        }
    }
}