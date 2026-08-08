package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.UserSettings
import com.example.data.WaterLog
import com.example.data.WaterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository
    
    val userSettings: StateFlow<UserSettings>
    val todayLogs: StateFlow<List<WaterLog>>
    val allLogs: StateFlow<List<WaterLog>>

    private val _showReminderDialog = MutableStateFlow(false)
    val showReminderDialog: StateFlow<Boolean> = _showReminderDialog.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Boire, 1: Historique, 2: Réglages
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WaterRepository(database.waterDao())

        viewModelScope.launch {
            repository.ensureDefaultSettings()
        }

        userSettings = repository.getUserSettings()
            .map { it ?: UserSettings() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserSettings()
            )

        todayLogs = repository.getTodayLogs()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        allLogs = repository.getAllLogs()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun setActiveTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun drinkWater(amountMl: Int = 250) {
        viewModelScope.launch {
            repository.addWaterLog(amountMl)
            _toastMessage.value = "+$amountMl ml d'eau enregistré ! Bravo ! 💧"
        }
    }

    fun removeLog(id: Int) {
        viewModelScope.launch {
            repository.removeWaterLog(id)
            _toastMessage.value = "Entrée supprimée"
        }
    }

    fun updateDailyGoalGlasses(glasses: Int) {
        val current = userSettings.value
        val updated = current.copy(dailyGoalGlasses = glasses.coerceIn(1, 25))
        viewModelScope.launch {
            repository.updateSettings(updated)
            _toastMessage.value = "Objectif mis à jour : $glasses verres (${glasses * current.glassSizeMl / 1000f} L)"
        }
    }

    fun updateSettings(newSettings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            _toastMessage.value = "Réglages enregistrés !"
        }
    }

    fun openReminderDialog() {
        _showReminderDialog.value = true
    }

    fun closeReminderDialog() {
        _showReminderDialog.value = false
    }

    fun confirmReminderDrink() {
        drinkWater(userSettings.value.glassSizeMl)
        _showReminderDialog.value = false
    }

    fun snoozeReminder() {
        _showReminderDialog.value = false
        _toastMessage.value = "Rappel décalé de 30 minutes ⏰"
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
