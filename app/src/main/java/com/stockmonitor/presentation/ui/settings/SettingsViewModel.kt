package com.stockmonitor.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockmonitor.domain.model.SettingsConfig
import com.stockmonitor.domain.repository.SettingsRepository
import com.stockmonitor.domain.repository.SettingsSaveResult
import com.stockmonitor.util.RefreshStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val refreshStateManager: RefreshStateManager
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.UpdateMorningStartTime -> updateEditedSettings { it.copy(morningStartTime = event.time) }
            is SettingsEvent.UpdateMorningEndTime -> updateEditedSettings { it.copy(morningEndTime = event.time) }
            is SettingsEvent.UpdateAfternoonStartTime -> updateEditedSettings { it.copy(afternoonStartTime = event.time) }
            is SettingsEvent.UpdateAfternoonEndTime -> updateEditedSettings { it.copy(afternoonEndTime = event.time) }
            is SettingsEvent.UpdateRefreshInterval -> updateInterval(event.interval)
            is SettingsEvent.UpdateApiSource -> updateApiSource(event.apiSource)
            is SettingsEvent.SaveSettings -> saveSettings()
            is SettingsEvent.ClearSaveStatus -> clearSaveStatus()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val settings = settingsRepository.getSettingsWithDefaults()
                _state.update {
                    it.copy(
                        isLoading = false,
                        settings = settings,
                        editedSettings = settings,
                        validationErrors = emptyMap()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        validationErrors = mapOf("general" to (e.message ?: "加载失败"))
                    )
                }
            }
        }
    }

    private fun updateEditedSettings(transform: (SettingsConfig) -> SettingsConfig) {
        _state.update {
            it.copy(
                editedSettings = transform(it.editedSettings),
                validationErrors = it.validationErrors - "general"
            )
        }
    }

    private fun updateInterval(interval: Int) {
        val validInterval = interval.coerceIn(1, 30)
        _state.update {
            it.copy(
                editedSettings = it.editedSettings.copy(refreshIntervalMinutes = validInterval),
                validationErrors = if (validInterval !in 1..30) {
                    it.validationErrors + ("refreshInterval" to "间隔必须在1-30分钟之间")
                } else {
                    it.validationErrors - "refreshInterval"
                }
            )
        }
    }

    private fun updateApiSource(apiSource: com.stockmonitor.domain.model.StockApiSource) {
        _state.update {
            it.copy(editedSettings = it.editedSettings.copy(apiSource = apiSource))
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = settingsRepository.saveSettings(_state.value.editedSettings)) {
                is SettingsSaveResult.SuccessNoReschedule -> {
                    val newSettings = _state.value.editedSettings
                    _state.update {
                        it.copy(
                            isLoading = false,
                            settings = newSettings,
                            editedSettings = newSettings,
                            saveSuccess = true
                        )
                    }
                }
                is SettingsSaveResult.SuccessRequiresReschedule -> {
                    val newSettings = _state.value.editedSettings
                    refreshStateManager.rescheduleWorker(newSettings.refreshIntervalMinutes.toLong())
                    _state.update {
                        it.copy(
                            isLoading = false,
                            settings = newSettings,
                            editedSettings = newSettings,
                            saveSuccess = true
                        )
                    }
                }
                is SettingsSaveResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            validationErrors = it.validationErrors + ("general" to result.message),
                            saveSuccess = false
                        )
                    }
                }
            }
        }
    }

    private fun clearSaveStatus() {
        _state.update { it.copy(saveSuccess = null) }
    }
}

sealed class SettingsEvent {
    data object LoadSettings : SettingsEvent()
    data class UpdateMorningStartTime(val time: String) : SettingsEvent()
    data class UpdateMorningEndTime(val time: String) : SettingsEvent()
    data class UpdateAfternoonStartTime(val time: String) : SettingsEvent()
    data class UpdateAfternoonEndTime(val time: String) : SettingsEvent()
    data class UpdateRefreshInterval(val interval: Int) : SettingsEvent()
    data class UpdateApiSource(val apiSource: com.stockmonitor.domain.model.StockApiSource) : SettingsEvent()
    data object SaveSettings : SettingsEvent()
    data object ClearSaveStatus : SettingsEvent()
}
