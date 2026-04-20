package com.englishwords.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.englishwords.data.preferences.LanguagePreferences
import com.englishwords.data.preferences.SpacePreferences
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
import com.englishwords.ui.screens.favorites.FavoritesScreen

// Быстрые и плавные анимации переходов
private const val TRANSITION_DURATION = 200
private const val FADE_DURATION = 150

private val enterTransition = slideInHorizontally(
    initialOffsetX = { it / 3 },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(FADE_DURATION))

private val exitTransition = fadeOut(animationSpec = tween(FADE_DURATION))

private val popEnterTransition = fadeIn(animationSpec = tween(FADE_DURATION))

private val popExitTransition = slideOutHorizontally(
    targetOffsetX = { it / 3 },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(FADE_DURATION))

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: WordRepository,
    themePreferences: ThemePreferences,
    languagePreferences: LanguagePreferences,
    spacePreferences: SpacePreferences,
    strings: Strings
) {
    // Храним конфигурацию сессии и результаты
    var sessionConfig by remember { mutableStateOf(SessionConfig()) }
    var sessionResult by remember { mutableStateOf<SessionResult?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { popEnterTransition },
            exitTransition = { exitTransition }
        ) {
            HomeScreen(
                repository = repository,
                themePreferences = themePreferences,
                languagePreferences = languagePreferences,
                spacePreferences = spacePreferences,
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
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        composable(
            route = Screen.SessionSetup.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SessionSetupScreen(
                repository = repository,
                spacePreferences = spacePreferences,
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
        
        composable(
            route = Screen.Learning.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            LearningScreen(
                repository = repository,
                spacePreferences = spacePreferences,
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
        
        composable(
            route = Screen.SessionResult.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SessionResultScreen(
                result = sessionResult,
                repository = repository,
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
        
        composable(
            route = Screen.AddWord.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            AddWordScreen(
                repository = repository,
                spacePreferences = spacePreferences,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Statistics.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            StatisticsScreen(
                repository = repository,
                spacePreferences = spacePreferences,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Settings.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SettingsScreen(
                themePreferences = themePreferences,
                languagePreferences = languagePreferences,
                spacePreferences = spacePreferences,
                wordRepository = repository,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.Favorites.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            FavoritesScreen(
                repository = repository,
                spacePreferences = spacePreferences,
                strings = strings,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
