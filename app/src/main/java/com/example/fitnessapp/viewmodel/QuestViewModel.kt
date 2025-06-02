package com.example.fitnessapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.QuestEntity
import com.example.fitnessapp.data.local.QuestStatus
import com.example.fitnessapp.data.repository.QuestRepository
import com.example.fitnessapp.wearos.SyncHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class QuestViewModel(
    application: Application,
    private val repository: QuestRepository
) : AndroidViewModel(application) {

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _km = MutableStateFlow(0f)
    val km: StateFlow<Float> = _km.asStateFlow()

    private val _calories = MutableStateFlow(0f)
    val calories: StateFlow<Float> = _calories.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    var targetKm: Float = 1f
        private set

    private var currentQuest: QuestEntity? = null
    private var simulationJob: Job? = null

    fun setQuest(quest: QuestEntity) {
        currentQuest = quest
        targetKm = quest.targetKm
        _steps.value = quest.steps
        _km.value = quest.steps * 0.5f / 1000f
        _calories.value = quest.steps * 0.04f
        _isPaused.value = quest.status == QuestStatus.PAUSED

        if (!_isPaused.value) startSimulation()
    }

    private fun simulateStep() {
        _steps.value += 100
        _km.value = _steps.value * 0.5f / 1000f
        _calories.value = _steps.value * 0.04f

        currentQuest?.let {
            SyncHelper.sendQuestData(
                context = getApplication(),
                questName = it.name,
                steps = _steps.value,
                km = _km.value,
                calo = _calories.value
            )
        }
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (isActive) {
                delay(2000L)
                simulateStep()
            }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
    }

    fun togglePause() {
        if (_isPaused.value) {
            startSimulation()
        } else {
            pauseQuest()
        }
        _isPaused.value = !_isPaused.value
    }

    fun pauseQuest() {
        stopSimulation()
        currentQuest?.let {
            val updated = it.copy(
                steps = _steps.value,
                status = QuestStatus.PAUSED
            )
            viewModelScope.launch {
                repository.updateQuest(updated)
            }
        }
    }

    fun stopAndSave() {
        stopSimulation()
        currentQuest?.let {
            val newStatus = if (_km.value >= targetKm) QuestStatus.COMPLETED else QuestStatus.PAUSED
            val updated = it.copy(
                steps = _steps.value,
                status = newStatus
            )
            viewModelScope.launch {
                repository.updateQuest(updated)
            }
        }
    }

    fun reset() {
        stopSimulation()
        _steps.value = 0
        _km.value = 0f
        _calories.value = 0f
        _isPaused.value = false
        currentQuest = null
    }
}
