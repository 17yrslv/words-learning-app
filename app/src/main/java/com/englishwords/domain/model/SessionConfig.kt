package com.englishwords.domain.model

data class SessionConfig(
    val wordCount: Int = 20,
    val sessionMode: SessionMode = SessionMode.ALL,
    val learningMode: LearningMode = LearningMode.EN_TO_RU,
    val answerType: AnswerType = AnswerType.TEXT_INPUT
)
