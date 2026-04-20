package com.englishwords.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spaces")
data class Space(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val shortName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
