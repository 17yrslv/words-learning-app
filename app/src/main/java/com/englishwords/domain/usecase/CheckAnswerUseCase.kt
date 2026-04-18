package com.englishwords.domain.usecase

class CheckAnswerUseCase {
    
    fun execute(
        userAnswer: String,
        correctTranslations: String
    ): Boolean {
        val normalizedUserAnswer = userAnswer
            .trim()
            .lowercase()
            .replace(Regex("[.,!?;:\"']"), "")
            .replace(Regex("\\s+"), " ")
        
        val translationsList = correctTranslations
            .split(",")
            .map { it.trim() }
        
        return translationsList.any { translation ->
            val normalizedTranslation = translation
                .trim()
                .lowercase()
                .replace(Regex("[.,!?;:\"']"), "")
                .replace(Regex("\\s+"), " ")
            
            normalizedUserAnswer == normalizedTranslation
        }
    }
}
