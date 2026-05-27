package com.agenda.ui.components.student

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agenda.data.model.Student
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EditStudentDialog(
    student: Student,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm") }

    var name by remember { mutableStateOf(student.name) }
    var phone by remember { mutableStateOf(student.phoneNumber ?: "") }
    var lessonsCount by remember { mutableStateOf(student.lessonsTaken.toString()) }
    var totalContractStr by remember { mutableStateOf(student.totalContractValue.toString()) }

    var newPaymentStr by remember { mutableStateOf("") }

    var upcomingLesson by remember { mutableStateOf(student.upcomingLesson) }
    var upcomingExam by remember { mutableStateOf(student.upcomingExam) }

    val parsedTotalContract = totalContractStr.toIntOrNull() ?: 0
    val parsedNewPayment = newPaymentStr.toIntOrNull() ?: 0

    val calculatedTotalPaid = student.moneyPaid + parsedNewPayment
    val calculatedRemaining = parsedTotalContract - calculatedTotalPaid

    val isNameValid = name.isNotBlank() && name.none { it.isDigit() }
    val isPhoneValid = phone.isBlank() || (phone.length == 10 && phone.all { it.isDigit() } && phone.startsWith("07"))
    val isLessonsCountValid = lessonsCount.isEmpty() || (lessonsCount.all { it.isDigit() } && (lessonsCount.toIntOrNull() ?: -1) >= 0)
    val isTotalContractValid = totalContractStr.isEmpty() || (totalContractStr.all { it.isDigit() } && (totalContractStr.toIntOrNull() ?: -1) >= 0)
    val isNewPaymentValid = newPaymentStr.isEmpty() || (newPaymentStr.all { it.isDigit() } && parsedNewPayment >= 0 && calculatedTotalPaid <= parsedTotalContract)

    val isFormValid = isNameValid && isPhoneValid && isLessonsCountValid && isTotalContractValid && isNewPaymentValid

    fun showDateTimePicker(initialDateTime: LocalDateTime?, onDateTimeSelected: (LocalDateTime) -> Unit) {
        val calendar = Calendar.getInstance()
        if (initialDateTime != null) {
            calendar.set(
                initialDateTime.year,
                initialDateTime.monthValue - 1,
                initialDateTime.dayOfMonth,
                initialDateTime.hour,
                initialDateTime.minute
            )
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showDatePicker(initialLocalDate: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        if (initialLocalDate != null) {
            calendar.set(
                initialLocalDate.year,
                initialLocalDate.monthValue - 1,
                initialLocalDate.dayOfMonth
            )
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editează Student") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nume") },
                    isError = !isNameValid && name.isNotEmpty(),
                    supportingText = {
                        if (!isNameValid && name.isNotEmpty()) {
                            Text("Numele nu poate conține cifre și nu poate fi gol")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Număr de telefon") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = !isPhoneValid && phone.isNotEmpty(),
                    supportingText = {
                        if (!isPhoneValid && phone.isNotEmpty()) {
                            Text("Trebuie să conțină 10 cifre și să înceapă cu 07")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lessonsCount,
                    onValueChange = { lessonsCount = it },
                    label = { Text("Ore efectuate") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isLessonsCountValid && lessonsCount.isNotEmpty(),
                    supportingText = {
                        if (!isLessonsCountValid && lessonsCount.isNotEmpty()) {
                            Text("Introduceți un număr valid (≥ 0)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = totalContractStr,
                    onValueChange = { totalContractStr = it },
                    label = { Text("Valoare Totală Contract (ron)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isTotalContractValid && totalContractStr.isNotEmpty(),
                    supportingText = {
                        if (!isTotalContractValid && totalContractStr.isNotEmpty()) {
                            Text("Introduceți o sumă validă (≥ 0)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPaymentStr,
                    onValueChange = { newPaymentStr = it },
                    label = { Text("Încasează Sumă Nouă") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isNewPaymentValid && newPaymentStr.isNotEmpty(),
                    supportingText = {
                        if (!isNewPaymentValid && newPaymentStr.isNotEmpty()) {
                            Text("Suma depășește valoarea rămasă sau este invalidă")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = "$calculatedTotalPaid ron (${student.moneyPaid} anterior)",
                    onValueChange = {},
                    label = { Text("Total Plătit (Actualizat)") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = "$calculatedRemaining ron",
                    onValueChange = {},
                    label = { Text("Sumă Rămasă (Calculată automat)") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = upcomingLesson?.format(dateTimeFormatter) ?: "Alege data și ora",
                    onValueChange = {},
                    label = { Text("Următoarea Oră") },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (upcomingLesson != null) {
                                IconButton(onClick = { upcomingLesson = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Șterge data", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDateTimePicker(upcomingLesson) { upcomingLesson = it } }
                )

                OutlinedTextField(
                    value = upcomingExam?.format(dateFormatter) ?: "Alege o dată",
                    onValueChange = {},
                    label = { Text("Data Examen") },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (upcomingExam != null) {
                                IconButton(onClick = { upcomingExam = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Șterge data", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker(upcomingExam) { upcomingExam = it } }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isFormValid,
                onClick = {
                    val updated = student.copy(
                        name = name.trim(),
                        phoneNumber = phone.trim().ifBlank { null },
                        lessonsTaken = lessonsCount.trim().toIntOrNull() ?: student.lessonsTaken,
                        totalContractValue = parsedTotalContract,
                        moneyPaid = calculatedTotalPaid,
                        upcomingLesson = upcomingLesson,
                        upcomingExam = upcomingExam
                    )
                    onConfirm(updated)
                }
            ) {
                Text("Salvează")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anulează")
            }
        }
    )
}