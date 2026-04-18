package com.englishwords.domain.model

import com.englishwords.data.local.Word

data class SessionResult(
    val totalWords: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val wordsWithErrors: List<WordError>,
    val correctWords: List<Word> = emptyList()
) {
    val accuracy: Int
        get() = if (totalWords > 0) (correctAnswers * 100 / totalWords) else 0
}

data class WordError(
    val word: Word,
    val userAnswer: String,
    val correctAnswers: List<String>
)
