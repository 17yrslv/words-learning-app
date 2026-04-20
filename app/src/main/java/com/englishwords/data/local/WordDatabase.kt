package com.englishwords.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Word::class, Space::class], version = 4, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun spaceDao(): SpaceDao
    
    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Создаем таблицу пространств
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS spaces (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                // Создаем пространство по умолчанию
                database.execSQL("INSERT INTO spaces (id, name, createdAt) VALUES (1, 'English', ${System.currentTimeMillis()})")
                
                // Добавляем колонку spaceId в таблицу words
                database.execSQL("ALTER TABLE words ADD COLUMN spaceId INTEGER NOT NULL DEFAULT 1")
            }
        }
        
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Добавляем колонку shortName в таблицу spaces
                database.execSQL("ALTER TABLE spaces ADD COLUMN shortName TEXT NOT NULL DEFAULT 'EN'")
            }
        }
        
        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Создаем пространство по умолчанию при первом создании базы данных
                        db.execSQL("INSERT INTO spaces (id, name, shortName, createdAt) VALUES (1, 'English', 'EN', ${System.currentTimeMillis()})")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
