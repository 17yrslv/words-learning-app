package com.englishwords.domain.usecase

import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode

class GetSessionWordsUseCase(
    private val repository: WordRepository
) {
    
    suspend fun execute(config: SessionConfig): List<Word> {
        val words = when (config.sessionMode) {
            SessionMode.NEW_WORDS -> {
                repository.getNewWords(config.wordCount)
            }
            SessionMode.REVIEW -> {
                repository.getWordsForReview(config.wordCount)
            }
            SessionMode.ALL -> {
                repository.getRandomWords(config.wordCount)
            }
            SessionMode.FAVORITES -> {
                repository.getFavoriteWords(config.wordCount)
            }
        }
        
        return words.shuffled()
    }
}
