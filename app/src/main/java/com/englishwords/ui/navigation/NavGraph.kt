package com.englishwords.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.ThemePreferences
import com.englishwords.data.repository.WordRepository
import com.englishwords.domain.model.SessionConfig
import com.englishwords.domain.model.SessionResult
import com.englishwords.ui.localization.Strings
import com.englishwords.ui.screens.home.HomeScreen
import com.englishwords.ui.screens.setup.SessionSetupScreen
import com.englishwords.ui.screens.learning.LearningScreen
import com.englishwords.ui.screens.result.SessionResultScreen
import com.englishwords.ui.screens.addword.AddWordScreen
import com.englishwords.ui.screens.statistics.StatisticsScreen
import com.englishwords.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: WordRepository,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    strings: Strings
) {
    // Храним конфигурацию сессии и результаты
    var sessionConfig by remember { mutableStateOf(SessionConfig()) }
    var sessionResult by remember { mutableStateOf<SessionResult?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                themePreferences = themePreferences,
                languagePreferences = languagePreferences,
                strings = strings,
                onNavigateToSetup = {
                    navController.navigate(Screen.SessionSetup.route)
                },
                onNavigateToAddWord = {
                    navController.navigate(Screen.AddWord.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.SessionSetup.route) {
            SessionSetupScreen(
                repository = repository,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartLearning = { config ->
                    sessionConfig = config
                    navController.navigate(Screen.Learning.route)
                }
            )
        }
        
        composable(Screen.Learning.route) {
            LearningScreen(
                repository = repository,
                config = sessionConfig,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onSessionComplete = { result: SessionResult ->
                    sessionResult = result
                    navController.navigate(Screen.SessionResult.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(Screen.SessionResult.route) {
            SessionResultScreen(
                result = sessionResult,
                strings = strings,
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onRepeatErrors = {
                    navController.navigate(Screen.Learning.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(Screen.AddWord.route) {
            AddWordScreen(
                repository = repository,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                repository = repository,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                themePreferences = themePreferences,
                languagePreferences = languagePreferences,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
