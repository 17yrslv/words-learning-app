package com.englishwords.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<Word>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)
    
    @Query("SELECT * FROM words WHERE repetitionLevel = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getNewWords(limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE nextReviewDate <= :today ORDER BY RANDOM() LIMIT :limit")
    suspend fun getWordsForReview(today: Long, limit: Int): List<Word>
    
    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWordsExcluding(excludeId: Long, limit: Int): List<Word>
    
    @Update
    suspend fun updateWord(word: Word)
    
    @Query("SELECT COUNT(*) FROM words")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE repetitionLevel >= 5")
    suspend fun getLearnedCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE repetitionLevel > 0 AND repetitionLevel < 5")
    suspend fun getLearningCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE nextReviewDate <= :today")
    suspend fun getReviewCount(today: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE repetitionLevel = 0")
    suspend fun getNewWordsCount(): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE nextReviewDate <= :today")
    suspend fun getWordsForReviewCount(today: Long): Int
    
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?
    
    @Query("SELECT englishWord FROM words")
    suspend fun getAllEnglishWords(): List<String>
    
    @Query("DELETE FROM words")
    suspend fun deleteAll()
}
