package com.englishwords.ui.screens.learning.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.englishwords.ui.theme.CorrectGreen
import com.englishwords.ui.theme.IncorrectRed

@Composable
fun MultipleChoiceOptions(
    options: List<String>,
    selectedOption: String?,
    correctOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Разбиваем на 2 ряда по 2 кнопки
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { option ->
                    OptionButton(
                        text = option,
                        isSelected = option == selectedOption,
                        isCorrect = correctOption != null && option == correctOption,
                        isIncorrect = correctOption != null && option == selectedOption && option != correctOption,
                        enabled = selectedOption == null,
                        onClick = { onOptionSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Если в ряду только одна кнопка, добавляем пустое пространство
                if (rowOptions.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = when {
        isCorrect -> ButtonDefaults.outlinedButtonColors(
            containerColor = CorrectGreen.copy(alpha = 0.2f),
            contentColor = CorrectGreen
        )
        isIncorrect -> ButtonDefaults.outlinedButtonColors(
            containerColor = IncorrectRed.copy(alpha = 0.2f),
            contentColor = IncorrectRed
        )
        isSelected -> ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        else -> ButtonDefaults.outlinedButtonColors()
    }
    
    val border = when {
        isCorrect -> BorderStroke(2.dp, CorrectGreen)
        isIncorrect -> BorderStroke(2.dp, IncorrectRed)
        isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(80.dp),
        enabled = enabled,
        colors = colors,
        border = border
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2
        )
    }
}
