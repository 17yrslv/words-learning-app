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
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId AND repetitionLevel = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getNewWords(spaceId: Long, limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId AND nextReviewDate <= :today ORDER BY RANDOM() LIMIT :limit")
    suspend fun getWordsForReview(spaceId: Long, today: Long, limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(spaceId: Long, limit: Int): List<Word>
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId AND id != :excludeId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWordsExcluding(spaceId: Long, excludeId: Long, limit: Int): List<Word>
    
    @Update
    suspend fun updateWord(word: Word)
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId")
    suspend fun getTotalCount(spaceId: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND repetitionLevel >= 5")
    suspend fun getLearnedCount(spaceId: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND repetitionLevel > 0 AND repetitionLevel < 5")
    suspend fun getLearningCount(spaceId: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND nextReviewDate <= :today")
    suspend fun getReviewCount(spaceId: Long, today: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND repetitionLevel = 0")
    suspend fun getNewWordsCount(spaceId: Long): Int
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND nextReviewDate <= :today")
    suspend fun getWordsForReviewCount(spaceId: Long, today: Long): Int
    
    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?
    
    @Query("SELECT englishWord FROM words WHERE spaceId = :spaceId")
    suspend fun getAllEnglishWords(spaceId: Long): List<String>
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId ORDER BY createdAt ASC")
    suspend fun getAllWords(spaceId: Long): List<Word>
    
    @Query("SELECT * FROM words WHERE spaceId = :spaceId AND isFavorite = 1 ORDER BY RANDOM() LIMIT :limit")
    suspend fun getFavoriteWords(spaceId: Long, limit: Int): List<Word>
    
    @Query("SELECT COUNT(*) FROM words WHERE spaceId = :spaceId AND isFavorite = 1")
    suspend fun getFavoriteWordsCount(spaceId: Long): Int
    
    @Query("DELETE FROM words")
    suspend fun deleteAll()
    
    @Query("DELETE FROM words WHERE spaceId = :spaceId")
    suspend fun deleteAllFromSpace(spaceId: Long)
}
