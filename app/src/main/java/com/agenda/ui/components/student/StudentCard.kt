package com.agenda.ui.components.student

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.agenda.data.model.Student
import com.agenda.data.model.StudentStatus
import com.agenda.viewmodel.StudentViewModel
import java.time.format.DateTimeFormatter

@Composable
fun StudentCard(
    student: Student,
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.weight(1f)) {
                    Text(student.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (student.status == StudentStatus.ACTIV) "Activ" else "Admis",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (student.status == StudentStatus.ACTIV) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = { showEditDialog = true }) { Icon(Icons.Default.Edit, "Edit") }
                IconButton(onClick = {
                    isExpanded = !isExpanded
                    if (isExpanded) {
                        viewModel.trackStudentAccess(student)
                    }
                }) {
                    Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, "Expand")
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Status(Activ/Admis):")
                    Switch(
                        checked = student.status == StudentStatus.ADMIS,
                        onCheckedChange = { isAdmis -> viewModel.updateStudent(student.copy(status = if (isAdmis) StudentStatus.ADMIS else StudentStatus.ACTIV)) }
                    )
                }

                DetailRow(label = "Telefon:", value = student.phoneNumber ?: "N/A", canCopy = student.phoneNumber != null)
                DetailRow(label = "Ore făcute:", value = "${student.lessonsTaken} ore")
                DetailRow(label = "Următoarea oră:", value = student.upcomingLesson?.format(dateTimeFormatter) ?: "N/A")
                DetailRow(label = "Data examenului:", value = student.upcomingExam?.format(dateFormatter) ?: "N/A")

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) { DetailRow(label = "Plătit:", value = "${student.moneyPaid} ron", isFinancial = true) }
                    Box(modifier = Modifier.weight(1f)) { DetailRow(label = "Rămas:", value = "${student.moneyToBePaid} ron", isFinancial = true) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Șterge elev")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Șterge elev?") },
            text = { Text("Această acțiune este permanentă.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteStudent(student); showDeleteDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Șterge") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Anulează") } }
        )
    }

    if (showEditDialog) {
        EditStudentDialog(student, { showEditDialog = false }, { viewModel.updateStudent(it); showEditDialog = false })
    }
}

@Composable
fun DetailRow(label: String, value: String, isFinancial: Boolean = false, canCopy: Boolean = false) {
    val clipboard = LocalClipboardManager.current
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isFinancial) FontWeight.Bold else FontWeight.Normal,
                color = if (isFinancial && label.contains("Rămas") && value != "0 ron") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            if (canCopy) {
                IconButton(onClick = { clipboard.setText(AnnotatedString(value)) }, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}