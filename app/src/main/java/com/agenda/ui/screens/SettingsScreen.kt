package com.agenda.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.agenda.viewmodel.StudentViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: StudentViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkModeOverride by viewModel.isDarkMode.collectAsState()
    val defaultContract by viewModel.defaultContractValue.collectAsState()
    val systemTheme = isSystemInDarkTheme()
    val isDarkMode = isDarkModeOverride ?: systemTheme

    var showContractDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            coroutineScope.launch {
                val json = viewModel.exportDatabaseToJson()
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(json.toByteArray())
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val json = context.contentResolver.openInputStream(it)?.bufferedReader().use { reader ->
                reader?.readText()
            }
            if (!json.isNullOrBlank()) {
                viewModel.importDatabaseFromJson(json)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Setări",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Aplicație",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingSwitchRow(
            icon = Icons.Default.DarkMode,
            title = "Mod Întunecat",
            subtitle = "Schimbă tema aplicației",
            checked = isDarkMode,
            onCheckedChange = { viewModel.setDarkMode(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Școală Auto",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingClickableRow(
            icon = Icons.Default.AttachMoney,
            title = "Valoare Contract Implicită",
            subtitle = "$defaultContract ron",
            onClick = { showContractDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Gestiune Date",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        SettingClickableRow(
            icon = Icons.Default.Download,
            title = "Exportă Baza de Date",
            subtitle = "Salvează o copie de siguranță (.json)",
            onClick = { exportLauncher.launch("agenda_auto_backup.json") }
        )

        SettingClickableRow(
            icon = Icons.Default.Upload,
            title = "Importă Baza de Date",
            subtitle = "Încarcă o copie de siguranță salvată anterior",
            onClick = { importLauncher.launch(arrayOf("application/json")) }
        )

        SettingClickableRow(
            icon = Icons.Default.DeleteSweep,
            title = "Curăță Elevii Admiși",
            subtitle = "Șterge definitiv elevii care au terminat",
            onClick = { showClearDialog = true },
            isDestructive = true
        )
    }

    if (showContractDialog) {
        var tempValue by remember { mutableStateOf(defaultContract.toString()) }
        AlertDialog(
            onDismissRequest = { showContractDialog = false },
            title = { Text("Valoare Contract") },
            text = {
                OutlinedTextField(
                    value = tempValue,
                    onValueChange = { tempValue = it },
                    label = { Text("Suma (ron)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    tempValue.toIntOrNull()?.let { viewModel.setDefaultContractValue(it) }
                    showContractDialog = false
                }) { Text("Salvează") }
            },
            dismissButton = {
                TextButton(onClick = { showContractDialog = false }) { Text("Anulează") }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Confirmare") },
            text = { Text("Sigur dorești să ștergi toți elevii cu statusul ADMIS? Această acțiune nu poate fi anulată.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAdmittedStudents() // <-- Aici era problema, acum este activat!
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Șterge") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Anulează") }
            }
        )
    }
}

@Composable
fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingClickableRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}