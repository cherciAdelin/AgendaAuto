package com.agenda.ui.components.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agenda.data.model.Student
import com.agenda.data.model.StudentStatus
import com.agenda.viewmodel.StudentViewModel

@Composable
fun AddStudent(
    viewModel: StudentViewModel,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    val defaultContract = viewModel.defaultContractValue.collectAsState().value

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var totalContractStr by remember { mutableStateOf(defaultContract.toString()) }
    var moneyPaidStr by remember { mutableStateOf("") }

    val isNameValid = name.isNotBlank() && name.none { it.isDigit() }
    val isPhoneValid = phone.isBlank() || (phone.length == 10 && phone.all { it.isDigit() } && phone.startsWith("07"))
    val isTotalContractValid = totalContractStr.isEmpty() || (totalContractStr.all { it.isDigit() } && (totalContractStr.toIntOrNull() ?: -1) >= 0)

    // Am pus toIntOrNull() in loc de toInt() ca sa evitam erori (crash) daca stergi tot din valoare contract
    val isPaidValid = moneyPaidStr.isEmpty() || (moneyPaidStr.all { it.isDigit() } && (moneyPaidStr.toIntOrNull() ?: -1) >= 0 && (moneyPaidStr.toIntOrNull() ?: 0) <= (totalContractStr.toIntOrNull() ?: 0))

    val isFormValid = isNameValid && isPhoneValid && isTotalContractValid && isPaidValid

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        val cleanTotalContract = totalContractStr.trim().let { if (it.isEmpty()) 0 else it.toIntOrNull() ?: 0 }
                        val cleanPaid = moneyPaidStr.trim().let { if (it.isEmpty()) 0 else it.toIntOrNull() ?: 0 }

                        val newStudent = Student(
                            name = name.trim(),
                            phoneNumber = phone.trim().ifBlank { null },
                            lessonsTaken = 0,
                            upcomingLesson = null,
                            upcomingExam = null,
                            totalContractValue = cleanTotalContract,
                            moneyPaid = cleanPaid,
                            status = StudentStatus.ACTIV
                        )
                        onSave(newStudent)
                    }
                },
                enabled = isFormValid
            ) {
                Text("Salvează")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anulează")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Adaugă Elev Nou",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nume și Prenume") },
                    isError = !isNameValid && name.isNotEmpty(),
                    supportingText = {
                        if (!isNameValid && name.isNotEmpty()) {
                            Text("Numele nu poate conține cifre și nu poate fi gol")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Număr Telefon") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = !isPhoneValid && phone.isNotEmpty(),
                    supportingText = {
                        if (!isPhoneValid && phone.isNotEmpty()) {
                            Text("Trebuie să conțină 10 cifre și să înceapă cu 07")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = totalContractStr,
                    onValueChange = { totalContractStr = it },
                    label = { Text("Valoare Contract (RON)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isTotalContractValid && totalContractStr.isNotEmpty(),
                    supportingText = {
                        if (!isTotalContractValid && totalContractStr.isNotEmpty()) {
                            Text("Introduceți o sumă validă (≥ 0)")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = moneyPaidStr,
                    onValueChange = { moneyPaidStr = it },
                    label = { Text("Sumă Plătită (RON)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = !isPaidValid && moneyPaidStr.isNotEmpty(),
                    supportingText = {
                        if (!isPaidValid && moneyPaidStr.isNotEmpty()) {
                            Text("Suma depășește valoarea contractului sau este invalidă")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}