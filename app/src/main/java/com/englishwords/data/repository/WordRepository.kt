package com.englishwords.data.repository

import com.englishwords.data.local.Word
import com.englishwords.data.local.WordDao

class WordRepository(private val wordDao: WordDao) {
    
    suspend fun insertAll(words: List<Word>) {
        wordDao.insertAll(words)
    }
    
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }
    
    suspend fun getNewWords(limit: Int): List<Word> {
        return wordDao.getNewWords(limit)
    }
    
    suspend fun getWordsForReview(limit: Int): List<Word> {
        val today = System.currentTimeMillis()
        return wordDao.getWordsForReview(today, limit)
    }
    
    suspend fun getRandomWords(limit: Int): List<Word> {
        return wordDao.getRandomWords(limit)
    }
    
    suspend fun getRandomWordsExcluding(excludeId: Long, limit: Int): List<Word> {
        return wordDao.getRandomWordsExcluding(excludeId, limit)
    }
    
    suspend fun updateWord(word: Word) {
        wordDao.updateWord(word)
    }
    
    suspend fun getTotalCount(): Int {
        return wordDao.getTotalCount()
    }
    
    suspend fun getLearnedCount(): Int {
        return wordDao.getLearnedCount()
    }
    
    suspend fun getLearningCount(): Int {
        return wordDao.getLearningCount()
    }
    
    suspend fun getReviewCount(): Int {
        val today = System.currentTimeMillis()
        return wordDao.getReviewCount(today)
    }
    
    suspend fun getWordById(id: Long): Word? {
        return wordDao.getWordById(id)
    }
    
    suspend fun getAllEnglishWords(): List<String> {
        return wordDao.getAllEnglishWords()
    }
}
