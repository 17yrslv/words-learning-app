package com.englishwords.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(space: Space): Long
    
    @Update
    suspend fun update(space: Space)
    
    @Query("SELECT * FROM spaces ORDER BY createdAt ASC")
    fun getAllSpaces(): Flow<List<Space>>
    
    @Query("SELECT * FROM spaces WHERE id = :id")
    suspend fun getSpaceById(id: Long): Space?
    
    @Query("SELECT COUNT(*) FROM spaces")
    suspend fun getSpacesCount(): Int
    
    @Query("DELETE FROM spaces WHERE id = :id")
    suspend fun deleteSpace(id: Long)
}
