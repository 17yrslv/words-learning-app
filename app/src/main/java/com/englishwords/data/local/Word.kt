package com.englishwords.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["spaceId", "repetitionLevel"]),
        Index(value = ["spaceId", "nextReviewDate"]),
        Index(value = ["spaceId", "isFavorite"])
    ]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val spaceId: Long = 1, // ID пространства, к которому принадлежит слово
    
    val englishWord: String,
    val russianTranslations: String,
    
    // Система интервальных повторений
    val repetitionLevel: Int = 0,
    val lastReviewDate: Long? = null,
    val nextReviewDate: Long? = null,
    
    // Статистика
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val consecutiveCorrect: Int = 0,
    
    val isFavorite: Boolean = false,
    
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getRussianTranslationsList(): List<String> {
        return russianTranslations
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
