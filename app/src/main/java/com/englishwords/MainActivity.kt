package com.englishwords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.englishwords.data.csv.CsvImporter
import com.englishwords.data.local.WordDatabase
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.navigation.NavGraph
import com.englishwords.ui.theme.EnglishWordsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var repository: WordRepository
    private lateinit var csvImporter: CsvImporter
    private lateinit var themePreferences: ThemePreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализация базы данных и репозитория
        val database = WordDatabase.getDatabase(applicationContext)
        repository = WordRepository(database.wordDao())
        csvImporter = CsvImporter(applicationContext, repository)
        themePreferences = ThemePreferences(applicationContext)
        
        setContent {
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)
            
            EnglishWordsTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isImporting by remember { mutableStateOf(true) }
                    var importError by remember { mutableStateOf<String?>(null) }
                    
                    LaunchedEffect(Unit) {
                        try {
                            // Всегда проверяем и импортируем новые слова из CSV
                            val count = csvImporter.importWordsFromCsv()
                            if (count > 0) {
                                println("Imported $count new words from CSV")
                            }
                            isImporting = false
                        } catch (e: Exception) {
                            importError = e.message
                            isImporting = false
                        }
                    }
                    
                    when {
                        isImporting -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator()
                                    Text("Загрузка слов...")
                                }
                            }
                        }
                        importError != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Ошибка загрузки данных",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = importError ?: "Unknown error",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        else -> {
                            val navController = rememberNavController()
                            NavGraph(
                                navController = navController,
                                repository = repository,
                                themePreferences = themePreferences
                            )
                        }
                    }
                }
            }
        }
    }
}
