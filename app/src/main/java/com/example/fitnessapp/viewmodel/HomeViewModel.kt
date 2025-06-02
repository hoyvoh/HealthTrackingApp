package com.example.fitnessapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.local.QuestEntity
import com.example.fitnessapp.data.local.QuestStatus
import com.example.fitnessapp.data.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: QuestRepository) : ViewModel() {

    private val _quests = MutableStateFlow<List<QuestEntity>>(emptyList())
    val quests: StateFlow<List<QuestEntity>> = _quests.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allQuests.collect { _quests.value = it }
        }
    }

    fun createQuest(name: String, targetKm: Float) {
        val newQuest = QuestEntity(
            name = name,
            targetKm = targetKm,
            status = QuestStatus.ONGOING
        )
        viewModelScope.launch {
            repository.insertQuest(newQuest)
        }
    }

    fun deleteQuest(quest: QuestEntity) {
        viewModelScope.launch {
            repository.deleteQuest(quest)
        }
    }
}