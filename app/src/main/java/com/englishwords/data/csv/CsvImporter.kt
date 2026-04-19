package com.englishwords.data.csv

import android.content.Context
import com.englishwords.R
import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class CsvImporter(
    private val context: Context,
    private val repository: WordRepository
) {
    
    suspend fun importWordsFromCsv(): Int = withContext(Dispatchers.IO) {
        val inputStream = context.resources.openRawResource(R.raw.words)
        return@withContext importFromInputStream(inputStream, checkDuplicates = true)
    }
    
    suspend fun importFromInputStream(inputStream: InputStream, checkDuplicates: Boolean = true): Int = withContext(Dispatchers.IO) {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        
        val words = mutableListOf<Word>()
        var lineCount = 0
        var isFullFormat = false
        
        reader.useLines { lines ->
            lines.forEach { line ->
                lineCount++
                // Проверить заголовок
                if (lineCount == 1) {
                    isFullFormat = line.contains("repetitionLevel")
                    return@forEach
                }
                
                val parsed = if (isFullFormat) {
                    parseFullCsvLine(line)
                } else {
                    parseSimpleCsvLine(line)
                }
                
                if (parsed != null) {
                    words.add(parsed)
                }
            }
        }
        
        val wordsToImport = if (checkDuplicates) {
            // Получить существующие английские слова из базы
            val existingEnglishWords = repository.getAllEnglishWords().toSet()
            // Фильтровать только новые слова
            words.filter { word -> word.englishWord !in existingEnglishWords }
        } else {
            words
        }
        
        // Сохранить слова в базу данных
        if (wordsToImport.isNotEmpty()) {
            repository.insertAll(wordsToImport)
        }
        
        return@withContext wordsToImport.size
    }
    
    private fun parseSimpleCsvLine(line: String): Word? {
        if (line.isBlank()) return null
        
        val parts = parseCsvFields(line)
        
        return if (parts.size >= 2) {
            Word(
                englishWord = parts[0],
                russianTranslations = parts[1]
            )
        } else null
    }
    
    private fun parseFullCsvLine(line: String): Word? {
        if (line.isBlank()) return null
        
        val parts = parseCsvFields(line)
        
        return if (parts.size >= 2) {
            Word(
                englishWord = parts[0],
                russianTranslations = parts[1],
                repetitionLevel = parts.getOrNull(2)?.toIntOrNull() ?: 0,
                lastReviewDate = parts.getOrNull(3)?.toLongOrNull(),
                nextReviewDate = parts.getOrNull(4)?.toLongOrNull(),
                correctCount = parts.getOrNull(5)?.toIntOrNull() ?: 0,
                incorrectCount = parts.getOrNull(6)?.toIntOrNull() ?: 0,
                consecutiveCorrect = parts.getOrNull(7)?.toIntOrNull() ?: 0,
                createdAt = parts.getOrNull(8)?.toLongOrNull() ?: System.currentTimeMillis()
            )
        } else null
    }
    
    private fun parseCsvFields(line: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    parts.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        parts.add(current.toString().trim())
        
        return parts
    }
    
    suspend fun isDataImported(): Boolean {
        return repository.getTotalCount() > 0
    }
}
