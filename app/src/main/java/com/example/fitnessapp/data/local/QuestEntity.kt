package com.example.fitnessapp.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class QuestStatus {
    ONGOING,
    PAUSED,
    COMPLETED
}

class Converters {
    @TypeConverter
    fun fromQuestStatus(value: QuestStatus): String = value.name

    @TypeConverter
    fun toQuestStatus(value: String): QuestStatus = QuestStatus.valueOf(value)
}

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetKm: Float,
    val steps: Int = 0,
    val status: QuestStatus,
    val timestamp: Long = System.currentTimeMillis()
)