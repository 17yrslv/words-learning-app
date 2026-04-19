package com.englishwords.ui.screens.addword

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.components.AnimatedButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    repository: WordRepository,
    strings: com.englishwords.ui.localization.Strings,
    onNavigateBack: () -> Unit
) {
    val viewModel: AddWordViewModel = viewModel(
        factory = AddWordViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.addWordTitle) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Английское слово
            OutlinedTextField(
                value = uiState.englishWord,
                onValueChange = { viewModel.onEnglishWordChange(it) },
                label = { Text(strings.englishWord) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isSaving
            )
            
            // Русские переводы
            OutlinedTextField(
                value = uiState.russianTranslations,
                onValueChange = { viewModel.onRussianTranslationsChange(it) },
                label = { Text(strings.russianTranslations) },
                supportingText = { Text(strings.separateByComma) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                enabled = !uiState.isSaving
            )
            
            // Сообщение об ошибке
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка сохранить
            AnimatedButton(
                onClick = { viewModel.saveWord() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(strings.save)
                }
            }
        }
    }
}
