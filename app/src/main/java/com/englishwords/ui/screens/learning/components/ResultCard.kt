package com.englishwords.ui.screens.learning.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.englishwords.ui.theme.CorrectGreen
import com.englishwords.ui.theme.IncorrectRed

@Composable
fun ResultCard(
    isCorrect: Boolean,
    userAnswer: String,
    correctAnswers: List<String>,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                CorrectGreen.copy(alpha = 0.2f)
            } else {
                IncorrectRed.copy(alpha = 0.2f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Заголовок
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCorrect) "✅ Правильно!" else "❌ Неправильно",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isCorrect) CorrectGreen else IncorrectRed
                )
            }
            
            Divider()
            
            // Ваш ответ
            Text(
                text = "Ваш ответ:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userAnswer,
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Правильные варианты
            if (!isCorrect || correctAnswers.size > 1) {
                Divider()
                Text(
                    text = if (isCorrect) "Все варианты перевода:" else "Правильные варианты:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                correctAnswers.forEach { answer ->
                    Text(
                        text = "• $answer",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Кнопка Далее
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Далее")
            }
        }
    }
}
