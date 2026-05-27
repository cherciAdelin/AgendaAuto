package com.agenda.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agenda.data.model.StudentStatus
import com.agenda.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.studentsState.collectAsState()
    val today = LocalDate.now()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM HH:mm") }
    val clipboard = LocalClipboardManager.current
    val activeStudentsCount = remember(students) { students.count { it.status == StudentStatus.ACTIV } }
    val totalPaid = remember(students) { students.filter { it.status == StudentStatus.ACTIV }.sumOf { it.moneyPaid } }
    val totalRemaining = remember(students) { students.filter { it.status == StudentStatus.ACTIV }.sumOf { it.moneyToBePaid } }

    val examAlerts = remember(students) {
        val nextWeek = today.plusDays(7)
        students.filter {
            it.upcomingExam != null &&
                    !it.upcomingExam!!.isBefore(today) &&
                    !it.upcomingExam!!.isAfter(nextWeek)
        }.sortedBy { it.upcomingExam }
    }

    val upcomingLessons = remember(students) {
        students.filter {
            it.upcomingLesson != null && !it.upcomingLesson!!.toLocalDate().isBefore(today)
        }.sortedBy { it.upcomingLesson }.take(5)
    }

    val recentStudents = remember(students) {
        students.filter { it.lastAccessedMillis > 0L }.sortedByDescending { it.lastAccessedMillis }.take(5)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Bine ați revenit!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(title = "Activi", value = activeStudentsCount.toString(), modifier = Modifier.weight(1f))
                StatCard(title = "Încasat", value = "${totalPaid} ron", modifier = Modifier.weight(1f))
                StatCard(title = "Restanțe", value = "${totalRemaining} ron", isAlert = totalRemaining > 0, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (examAlerts.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Alertă Examen")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Examene în următoarele 7 zile!", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        examAlerts.forEach { student ->
                            val daysLeft = ChronoUnit.DAYS.between(today, student.upcomingExam)
                            val dayString = if (daysLeft == 0L) "Azi!" else if (daysLeft == 1L) "Mâine!" else "în $daysLeft zile"
                            Text("• ${student.name} - $dayString", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (upcomingLessons.isNotEmpty()) {
            item {
                Text(
                    text = "Urmează Curând:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(upcomingLessons) { student ->
                        Card(
                            modifier = Modifier.width(160.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(student.name, fontWeight = FontWeight.Bold, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Data: ${student.upcomingLesson?.format(dateFormatter)}")
                                Row {
                                    Text(
                                        student.phoneNumber ?: "Fără telefon",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    IconButton(onClick = { student.phoneNumber?.let { clipboard.setText(AnnotatedString(it)) } }, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text(
                text = "Ultimii elevi accesați:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (recentStudents.isEmpty()) {
            item {
                Text(
                    text = "Nu există elevi accesați recent.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(recentStudents) { student ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (student.status == StudentStatus.ACTIV) "Activ" else "Admis",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Restanță: ${student.moneyToBePaid} ron",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (student.moneyToBePaid > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, isAlert: Boolean = false, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}