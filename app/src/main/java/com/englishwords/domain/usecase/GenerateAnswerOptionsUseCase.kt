package com.englishwords.domain.usecase

import com.englishwords.data.local.Word
import com.englishwords.data.preferences.SpacePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.LearningMode
import kotlinx.coroutines.flow.first

class GenerateAnswerOptionsUseCase(
    private val repository: WordRepository,
    private val spacePreferences: SpacePreferences
) {
    
    suspend fun execute(
        currentWord: Word,
        learningMode: LearningMode
    ): List<String> {
        // Получаем правильный ответ
        val correctAnswer = when (learningMode) {
            LearningMode.EN_TO_RU -> {
                // Берём первый русский перевод
                currentWord.getRussianTranslationsList().firstOrNull() ?: ""
            }
            LearningMode.RU_TO_EN -> {
                currentWord.englishWord
            }
            else -> return emptyList()
        }
        
        // Получаем 3 случайных слова (исключая текущее)
        val spaceId = spacePreferences.currentSpaceId.first()
        val randomWords = repository.getRandomWordsExcluding(spaceId, currentWord.id, 3)
        
        // Формируем неправильные варианты
        val wrongAnswers = randomWords.map { word ->
            when (learningMode) {
                LearningMode.EN_TO_RU -> {
                    word.getRussianTranslationsList().firstOrNull() ?: ""
                }
                LearningMode.RU_TO_EN -> {
                    word.englishWord
                }
                else -> ""
            }
        }.filter { it.isNotEmpty() }
        
        // Если не хватает неправильных вариантов, возвращаем пустой список
        if (wrongAnswers.size < 3) {
            return emptyList()
        }
        
        // Объединяем правильный ответ с неправильными и перемешиваем
        val allOptions = (listOf(correctAnswer) + wrongAnswers.take(3)).shuffled()
        
        return allOptions
    }
}
