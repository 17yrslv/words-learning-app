package com.englishwords.ui.localization

data class Strings(
    // Common
    val back: String,
    val save: String,
    val cancel: String,
    val next: String,
    val check: String,
    val home: String,
    
    // Home Screen
    val appTitle: String,
    val statistics: String,
    val totalWords: String,
    val learning: String,
    val learned: String,
    val reviewToday: String,
    val startLearning: String,
    val addWord: String,
    
    // Session Setup
    val sessionSetup: String,
    val learningMode: String,
    val enToRu: String,
    val ruToEn: String,
    val mixedMode: String,
    val answerType: String,
    val textInput: String,
    val multipleChoice: String,
    val wordCount: String,
    val wordCountRange: String,
    val wordType: String,
    val newWords: String,
    val review: String,
    val allWords: String,
    val favoriteWords: String,
    val start: String,
    
    // Learning Screen
    val learningTitle: String,
    val progress: String,
    
    // Session Result
    val sessionResults: String,
    val excellentWork: String,
    val goodWork: String,
    val keepPracticing: String,
    val sessionStats: String,
    val correct: String,
    val incorrect: String,
    val accuracy: String,
    val correctWords: String,
    val incorrectWords: String,
    val correctAnswer: String,
    val yourAnswer: String,
    val repeatErrors: String,
    
    // Add Word Screen
    val addWordTitle: String,
    val englishWord: String,
    val russianTranslations: String,
    val separateByComma: String,
    
    // Statistics Screen
    val generalStats: String,
    val learningProgress: String,
    val information: String,
    val spacedRepetitionInfo: String,
    val levelsInfo: String,
    val onReview: String,
    val learnedProgress: String,
    
    // Other
    val loadingWords: String,
    val dataLoadError: String,
    val noResultsData: String,
    val switchToLightTheme: String,
    val switchToDarkTheme: String,
    val switchToEnglish: String,
    val switchToRussian: String,
    
    // Learning Screen - additional
    val exit: String,
    val nextButton: String,
    
    // Session Result - additional
    val totalWordsLabel: String,
    val collapse: String,
    val expand: String,
    
    // Settings Screen
    val settings: String,
    val theme: String,
    val darkTheme: String,
    val lightTheme: String,
    val language: String,
    val selectLanguage: String,
    
    // Favorites Screen
    val favorites: String,
    val noFavorites: String,
    val removeFromFavorites: String
)

val EnglishStrings = Strings(
    // Common
    back = "Back",
    save = "Save",
    cancel = "Cancel",
    next = "Next",
    check = "Check",
    home = "Home",
    
    // Home Screen
    appTitle = "English Words Learning",
    statistics = "Statistics",
    totalWords = "Total words:",
    learning = "Learning:",
    learned = "Learned:",
    reviewToday = "Review today:",
    startLearning = "Start Learning",
    addWord = "Add Word",
    
    // Session Setup
    sessionSetup = "Session Setup",
    learningMode = "Learning mode:",
    enToRu = "EN → RU",
    ruToEn = "RU → EN",
    mixedMode = "Mixed mode",
    answerType = "Answer type:",
    textInput = "Text input",
    multipleChoice = "Multiple choice",
    wordCount = "Word count:",
    wordCountRange = "from 5 to 50",
    wordType = "Word type:",
    newWords = "New words",
    review = "Review",
    allWords = "All words",
    favoriteWords = "Favorite words",
    start = "Start",
    
    // Learning Screen
    learningTitle = "Learning",
    progress = "Progress",
    
    // Session Result
    sessionResults = "Session Results",
    excellentWork = "Excellent work!",
    goodWork = "Good work!",
    keepPracticing = "Keep practicing!",
    sessionStats = "Session statistics",
    correct = "Correct:",
    incorrect = "Incorrect:",
    accuracy = "Accuracy:",
    correctWords = "Correct words",
    incorrectWords = "Incorrect words",
    correctAnswer = "Correct:",
    yourAnswer = "Your answer:",
    repeatErrors = "Repeat errors",
    
    // Add Word Screen
    addWordTitle = "Add Word",
    englishWord = "English word",
    russianTranslations = "Russian translations",
    separateByComma = "Separate by comma",
    
    // Statistics Screen
    generalStats = "General statistics",
    learningProgress = "Learning progress",
    information = "Information",
    spacedRepetitionInfo = "Spaced repetition system helps to memorize words effectively.",
    levelsInfo = "Words go through 6 memory levels with intervals: 0, 1, 3, 7, 14, 30 days.",
    onReview = "On review:",
    learnedProgress = "Learned",
    
    // Other
    loadingWords = "Loading words...",
    dataLoadError = "Data loading error",
    noResultsData = "No results data",
    switchToLightTheme = "Switch to light theme",
    switchToDarkTheme = "Switch to dark theme",
    switchToEnglish = "Switch to English",
    switchToRussian = "Switch to Russian",
    
    // Learning Screen - additional
    exit = "Exit",
    nextButton = "Next",
    
    // Session Result - additional
    totalWordsLabel = "Total words:",
    collapse = "Collapse",
    expand = "Expand",
    
    // Settings Screen
    settings = "Settings",
    theme = "Theme",
    darkTheme = "Dark theme",
    lightTheme = "Light theme",
    language = "Language",
    selectLanguage = "Select language",
    
    // Favorites Screen
    favorites = "Favorites",
    noFavorites = "No favorite words yet",
    removeFromFavorites = "Remove from favorites"
)

val RussianStrings = Strings(
    // Common
    back = "Назад",
    save = "Сохранить",
    cancel = "Отмена",
    next = "Далее",
    check = "Проверить",
    home = "На главную",
    
    // Home Screen
    appTitle = "English Words Learning",
    statistics = "Статистика",
    totalWords = "Всего слов:",
    learning = "Изучается:",
    learned = "Выучено:",
    reviewToday = "На повторении сегодня:",
    startLearning = "Начать обучение",
    addWord = "Добавить слово",
    
    // Session Setup
    sessionSetup = "Настройка сессии",
    learningMode = "Режим обучения:",
    enToRu = "EN → RU",
    ruToEn = "RU → EN",
    mixedMode = "Смешанный режим",
    answerType = "Тип ответа:",
    textInput = "Ввод текста",
    multipleChoice = "Выбор из вариантов",
    wordCount = "Количество слов:",
    wordCountRange = "от 5 до 50",
    wordType = "Тип слов:",
    newWords = "Новые слова",
    review = "На повторении",
    allWords = "Все слова",
    favoriteWords = "Избранные слова",
    start = "Начать",
    
    // Learning Screen
    learningTitle = "Обучение",
    progress = "Прогресс",
    
    // Session Result
    sessionResults = "Результаты сессии",
    excellentWork = "Отличная работа!",
    goodWork = "Хорошо!",
    keepPracticing = "Продолжай практиковаться!",
    sessionStats = "Статистика сессии",
    correct = "Правильно:",
    incorrect = "Неправильно:",
    accuracy = "Точность:",
    correctWords = "Правильные слова",
    incorrectWords = "Неправильные слова",
    correctAnswer = "Правильно:",
    yourAnswer = "Ваш ответ:",
    repeatErrors = "Повторить ошибки",
    
    // Add Word Screen
    addWordTitle = "Добавить слово",
    englishWord = "Английское слово",
    russianTranslations = "Русские переводы",
    separateByComma = "Через запятую",
    
    // Statistics Screen
    generalStats = "Общая статистика",
    learningProgress = "Прогресс изучения",
    information = "Информация",
    spacedRepetitionInfo = "Система интервальных повторений помогает эффективно запоминать слова.",
    levelsInfo = "Слова проходят через 6 уровней запоминания с интервалами: 0, 1, 3, 7, 14, 30 дней.",
    onReview = "На повторении:",
    learnedProgress = "Изучено",
    
    // Other
    loadingWords = "Загрузка слов...",
    dataLoadError = "Ошибка загрузки данных",
    noResultsData = "Нет данных о результатах",
    switchToLightTheme = "Переключить на светлую тему",
    switchToDarkTheme = "Переключить на тёмную тему",
    switchToEnglish = "Переключить на английский",
    switchToRussian = "Переключить на русский",
    
    // Learning Screen - additional
    exit = "Выход",
    nextButton = "Далее",
    
    // Session Result - additional
    totalWordsLabel = "Всего слов:",
    collapse = "Свернуть",
    expand = "Развернуть",
    
    // Settings Screen
    settings = "Настройки",
    theme = "Тема",
    darkTheme = "Тёмная тема",
    lightTheme = "Светлая тема",
    language = "Язык",
    selectLanguage = "Выберите язык",
    
    // Favorites Screen
    favorites = "Избранные",
    noFavorites = "Нет избранных слов",
    removeFromFavorites = "Убрать из избранного"
)

fun getStrings(language: String): Strings {
    return when (language) {
        "ru" -> RussianStrings
        else -> EnglishStrings
    }
}
