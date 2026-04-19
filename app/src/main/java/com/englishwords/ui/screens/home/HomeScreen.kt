package com.englishwords.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.components.AnimatedButton
import com.englishwords.ui.localization.Strings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: WordRepository,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    strings: Strings,
    onNavigateToSetup: () -> Unit,
    onNavigateToAddWord: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(repository)
    )
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)
    val language by languagePreferences.language.collectAsState(initial = "en")
    val scope = rememberCoroutineScope()
    
    // Обновляем статистику при каждом возврате на экран
    LaunchedEffect(Unit) {
        viewModel.loadStatistics()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(48.dp))
                        Text(
                            text = "Wordy",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = strings.settings
                        )
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                // Статистика
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.statistics,
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        StatRow(strings.totalWords, uiState.totalWords.toString())
                        StatRow(strings.learning, uiState.learningWords.toString())
                        StatRow(strings.learned, uiState.learnedWords.toString())
                        StatRow(strings.reviewToday, uiState.reviewWords.toString())
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Кнопки
                AnimatedButton(
                    onClick = onNavigateToSetup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.startLearning, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToAddWord,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.addWord, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToStatistics,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.statistics, style = MaterialTheme.typography.titleMedium)
                }
                
                OutlinedButton(
                    onClick = onNavigateToFavorites,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(strings.favorites, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
