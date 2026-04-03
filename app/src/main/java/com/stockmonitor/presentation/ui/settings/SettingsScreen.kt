package com.stockmonitor.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockmonitor.R
import com.stockmonitor.domain.model.SettingsConfig
import com.stockmonitor.domain.model.StockApiSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                actions = {
                    TextButton(
                        onClick = { viewModel.onEvent(SettingsEvent.SaveSettings) },
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading && state.settings == SettingsConfig.DEFAULT) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TradingTimeSection(
                    settings = state.editedSettings,
                    onTimeChange = { field, time ->
                        when (field) {
                            "morningStart" -> viewModel.onEvent(SettingsEvent.UpdateMorningStartTime(time))
                            "morningEnd" -> viewModel.onEvent(SettingsEvent.UpdateMorningEndTime(time))
                            "afternoonStart" -> viewModel.onEvent(SettingsEvent.UpdateAfternoonStartTime(time))
                            "afternoonEnd" -> viewModel.onEvent(SettingsEvent.UpdateAfternoonEndTime(time))
                        }
                    }
                )

                IntervalSection(
                    interval = state.editedSettings.refreshIntervalMinutes,
                    onIntervalChange = { viewModel.onEvent(SettingsEvent.UpdateRefreshInterval(it)) },
                    error = state.validationErrors["refreshInterval"]
                )

                ApiSourceSection(
                    selectedSource = state.editedSettings.apiSource,
                    onSourceChange = { viewModel.onEvent(SettingsEvent.UpdateApiSource(it)) }
                )

                state.validationErrors["general"]?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TradingTimeSection(
    settings: SettingsConfig,
    onTimeChange: (field: String, time: String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf<String?>(null) }
    var editingField by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.trading_time_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TimeRow(
                label = stringResource(R.string.morning_start_time),
                value = settings.morningStartTime,
                onClick = {
                    editingField = "morningStart"
                    showTimePicker = settings.morningStartTime
                }
            )

            TimeRow(
                label = stringResource(R.string.morning_end_time),
                value = settings.morningEndTime,
                onClick = {
                    editingField = "morningEnd"
                    showTimePicker = settings.morningEndTime
                }
            )

            TimeRow(
                label = stringResource(R.string.afternoon_start_time),
                value = settings.afternoonStartTime,
                onClick = {
                    editingField = "afternoonStart"
                    showTimePicker = settings.afternoonStartTime
                }
            )

            TimeRow(
                label = stringResource(R.string.afternoon_end_time),
                value = settings.afternoonEndTime,
                onClick = {
                    editingField = "afternoonEnd"
                    showTimePicker = settings.afternoonEndTime
                }
            )
        }
    }

    showTimePicker?.let { initialTime ->
        TimePickerDialogContent(
            initialTime = initialTime,
            onDismiss = { showTimePicker = null },
            onConfirm = { selectedTime ->
                onTimeChange(editingField, selectedTime)
                showTimePicker = null
            }
        )
    }
}

@Composable
private fun TimeRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialogContent(
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val parts = initialTime.split(":")
    val initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9
    val initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            onConfirm("$hour:$minute")
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun IntervalSection(
    interval: Int,
    onIntervalChange: (Int) -> Unit,
    error: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.interval_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.interval_current_value, interval),
                style = MaterialTheme.typography.bodyLarge
            )

            Slider(
                value = interval.toFloat(),
                onValueChange = { onIntervalChange(it.toInt()) },
                valueRange = 1f..30f,
                steps = 28,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.interval_min),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = stringResource(R.string.interval_max),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = interval.toString(),
                onValueChange = { newValue ->
                    newValue.toIntOrNull()?.let { onIntervalChange(it) }
                },
                label = { Text(stringResource(R.string.interval_input_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = error?.let { { Text(it) } }
            )
        }
    }
}

@Composable
private fun ApiSourceSection(
    selectedSource: StockApiSource,
    onSourceChange: (StockApiSource) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.api_source_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedSource.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                StockApiSource.entries.forEach { source ->
                    DropdownMenuItem(
                        text = { Text(source.displayName) },
                        onClick = {
                            onSourceChange(source)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
