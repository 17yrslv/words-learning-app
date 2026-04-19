package com.englishwords.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SessionSetup : Screen("session_setup")
    object Learning : Screen("learning")
    object SessionResult : Screen("session_result")
    object AddWord : Screen("add_word")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
}
