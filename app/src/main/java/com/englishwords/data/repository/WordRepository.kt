package com.englishwords.data.repository

import com.englishwords.data.local.Space
import com.englishwords.data.local.SpaceDao
import com.englishwords.data.local.Word
import com.englishwords.data.local.WordDao
import com.englishwords.data.local.WordStatistics
import kotlinx.coroutines.flow.Flow

class WordRepository(
    private val wordDao: WordDao,
    private val spaceDao: SpaceDao
) {
    
    suspend fun insertAll(words: List<Word>) {
        wordDao.insertAll(words)
    }
    
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
    
    suspend fun getNewWords(spaceId: Long, limit: Int): List<Word> {
        return wordDao.getNewWords(spaceId, limit)
    }
    
    suspend fun getWordsForReview(spaceId: Long, limit: Int): List<Word> {
        val today = System.currentTimeMillis()
        return wordDao.getWordsForReview(spaceId, today, limit)
    }
    
    suspend fun getRandomWords(spaceId: Long, limit: Int): List<Word> {
        return wordDao.getRandomWords(spaceId, limit)
    }
    
    suspend fun getRandomWordsExcluding(spaceId: Long, excludeId: Long, limit: Int): List<Word> {
        return wordDao.getRandomWordsExcluding(spaceId, excludeId, limit)
    }
    
    suspend fun updateWord(word: Word) {
        wordDao.updateWord(word)
    }
    
    suspend fun getTotalCount(spaceId: Long): Int {
        return wordDao.getTotalCount(spaceId)
    }
    
    suspend fun getLearnedCount(spaceId: Long): Int {
        return wordDao.getLearnedCount(spaceId)
    }
    
    suspend fun getLearningCount(spaceId: Long): Int {
        return wordDao.getLearningCount(spaceId)
    }
    
    suspend fun getReviewCount(spaceId: Long): Int {
        val today = System.currentTimeMillis()
        return wordDao.getReviewCount(spaceId, today)
    }
    
    // Оптимизированный метод для получения всей статистики за один запрос
    suspend fun getStatistics(spaceId: Long): WordStatistics {
        val today = System.currentTimeMillis()
        return wordDao.getStatistics(spaceId, today)
    }
    
    suspend fun getWordById(id: Long): Word? {
        return wordDao.getWordById(id)
    }
    
    suspend fun getAllEnglishWords(spaceId: Long): List<String> {
        return wordDao.getAllEnglishWords(spaceId)
    }
    
    suspend fun getNewWordsCount(spaceId: Long): Int {
        return wordDao.getNewWordsCount(spaceId)
    }
    
    suspend fun getWordsForReviewCount(spaceId: Long): Int {
        val today = System.currentTimeMillis()
        return wordDao.getWordsForReviewCount(spaceId, today)
    }
    
    suspend fun getAllWords(spaceId: Long): List<Word> {
        return wordDao.getAllWords(spaceId)
    }
    
    suspend fun getFavoriteWords(spaceId: Long, limit: Int): List<Word> {
        return wordDao.getFavoriteWords(spaceId, limit)
    }
    
    suspend fun getFavoriteWordsCount(spaceId: Long): Int {
        return wordDao.getFavoriteWordsCount(spaceId)
    }
    
    suspend fun toggleFavorite(word: Word) {
        wordDao.updateWord(word.copy(isFavorite = !word.isFavorite))
    }
    
    suspend fun deleteAll() {
        wordDao.deleteAll()
    }
    
    // Методы для работы с пространствами
    fun getAllSpaces(): Flow<List<Space>> {
        return spaceDao.getAllSpaces()
    }
    
    suspend fun getSpaceById(id: Long): Space? {
        return spaceDao.getSpaceById(id)
    }
    
    suspend fun createSpace(name: String, shortName: String): Long {
        val space = Space(name = name, shortName = shortName)
        return spaceDao.insert(space)
    }
    
    suspend fun updateSpace(space: Space) {
        spaceDao.update(space)
    }
    
    suspend fun deleteSpace(id: Long) {
        // Сначала удаляем все слова из пространства
        wordDao.deleteAllFromSpace(id)
        // Затем удаляем само пространство
        spaceDao.deleteSpace(id)
    }
}
