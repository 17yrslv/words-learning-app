package com.englishwords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.englishwords.data.csv.CsvImporter
import com.englishwords.data.local.WordDatabase
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.ui.localization.getStrings
import com.englishwords.ui.navigation.NavGraph
import com.englishwords.ui.theme.EnglishWordsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var repository: WordRepository
    private lateinit var csvImporter: CsvImporter
    private lateinit var themePreferences: ThemePreferences
    private lateinit var languagePreferences: LanguagePreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Включаем режим edge-to-edge для безрамочных экранов
        enableEdgeToEdge()
        
        // Инициализация базы данных и репозитория
        val database = WordDatabase.getDatabase(applicationContext)
        repository = WordRepository(database.wordDao())
        csvImporter = CsvImporter(applicationContext, repository)
        themePreferences = ThemePreferences(applicationContext)
        languagePreferences = LanguagePreferences(applicationContext)
        
        setContent {
            val isDarkTheme by themePreferences.isDarkTheme.collectAsState(initial = true)
            val language by languagePreferences.language.collectAsState(initial = "en")
            val strings = remember(language) { getStrings(language) }
            
            // Обновляем цвет иконок статус-бара в зависимости от темы
            LaunchedEffect(isDarkTheme) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                    isAppearanceLightNavigationBars = !isDarkTheme
                }
            }
            
            EnglishWordsTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isImporting by remember { mutableStateOf(true) }
                    var importError by remember { mutableStateOf<String?>(null) }
                    
                    LaunchedEffect(Unit) {
                        try {
                            // Проверяем, был ли уже выполнен первоначальный импорт
                            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            val isFirstLaunch = !prefs.getBoolean("initial_import_done", false)
                            
                            if (isFirstLaunch) {
                                // Импортируем слова только при первом запуске
                                val count = csvImporter.importWordsFromCsv()
                                if (count > 0) {
                                    println("Imported $count new words from CSV")
                                }
                                // Отмечаем, что импорт выполнен
                                prefs.edit().putBoolean("initial_import_done", true).apply()
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
                                    Text(strings.loadingWords)
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
                                        text = strings.dataLoadError,
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
                                themePreferences = themePreferences,
                                languagePreferences = languagePreferences,
                                strings = strings
                            )
                        }
                    }
                }
            }
        }
    }
}
