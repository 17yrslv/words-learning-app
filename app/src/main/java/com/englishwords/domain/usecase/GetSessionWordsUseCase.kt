package com.englishwords.domain.usecase

import com.englishwords.data.local.Word
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionMode
import kotlinx.coroutines.flow.first

class GetSessionWordsUseCase(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) {
    
    suspend fun execute(config: SessionConfig): List<Word> {
        val spaceId = spacePreferences.currentSpaceId.first()
        val words = when (config.sessionMode) {
            SessionMode.NEW_WORDS -> {
                repository.getNewWords(spaceId, config.wordCount)
            }
            SessionMode.REVIEW -> {
                repository.getWordsForReview(spaceId, config.wordCount)
            }
            SessionMode.ALL -> {
                repository.getRandomWords(spaceId, config.wordCount)
            }
            SessionMode.FAVORITES -> {
                repository.getFavoriteWords(spaceId, config.wordCount)
            }
        }
        
        return words.shuffled()
    }
}
