package com.example.fitnessapp.data.repository

import com.example.fitnessapp.data.local.QuestDao
import com.example.fitnessapp.data.local.QuestEntity
import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questDao: QuestDao) {

    val allQuests: Flow<List<QuestEntity>> = questDao.getAllQuests()

    suspend fun insertQuest(quest: QuestEntity) {
        questDao.insertQuest(quest)
    }

    suspend fun updateQuest(quest: QuestEntity) {
        questDao.updateQuest(quest)
    }

    suspend fun deleteQuest(quest: QuestEntity) {
        questDao.deleteQuest(quest)
    }

    suspend fun getQuestById(id: Int): QuestEntity? {
        return questDao.getQuestById(id)
    }
}
