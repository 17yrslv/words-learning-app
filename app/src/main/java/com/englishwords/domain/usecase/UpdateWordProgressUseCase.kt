package com.englishwords.domain.usecase

import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository

class UpdateWordProgressUseCase(
    private val repository: WordRepository
) {
    
    suspend fun execute(word: Word, isCorrect: Boolean) {
        val updatedWord = if (isCorrect) {
            updateForCorrectAnswer(word)
        } else {
            updateForIncorrectAnswer(word)
        }
        
        repository.updateWord(updatedWord)
    }
    
    private fun updateForCorrectAnswer(word: Word): Word {
        val newLevel = (word.repetitionLevel + 1).coerceAtMost(5)
        val nextReviewDate = calculateNextReviewDate(newLevel)
        
        return word.copy(
            repetitionLevel = newLevel,
            lastReviewDate = System.currentTimeMillis(),
            nextReviewDate = nextReviewDate,
            correctCount = word.correctCount + 1,
            consecutiveCorrect = word.consecutiveCorrect + 1
        )
    }
    
    private fun updateForIncorrectAnswer(word: Word): Word {
        val newLevel = 0
        val nextReviewDate = calculateNextReviewDate(newLevel)
        
        return word.copy(
            repetitionLevel = newLevel,
            lastReviewDate = System.currentTimeMillis(),
            nextReviewDate = nextReviewDate,
            incorrectCount = word.incorrectCount + 1,
            consecutiveCorrect = 0
        )
    }
    
    private fun calculateNextReviewDate(level: Int): Long {
        val daysUntilNextReview = when (level) {
            0 -> 0  // сегодня
            1 -> 1
            2 -> 3
            3 -> 7
            4 -> 14
            5 -> 30
            else -> 0
        }
        
        return System.currentTimeMillis() + (daysUntilNextReview * 24 * 60 * 60 * 1000L)
    }
}
