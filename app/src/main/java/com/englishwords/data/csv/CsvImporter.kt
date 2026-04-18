package com.englishwords.data.csv

import android.content.Context
import com.englishwords.R
import com.englishwords.data.local.Word
import com.englishwords.data.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class CsvImporter(
    private val context: Context,
    private val repository: WordRepository
) {
    
    suspend fun importWordsFromCsv(): Int = withContext(Dispatchers.IO) {
        val inputStream = context.resources.openRawResource(R.raw.words)
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
        
        val words = mutableListOf<Word>()
        var lineCount = 0
        
        reader.useLines { lines ->
            lines.forEach { line ->
                lineCount++
                // Пропустить заголовок
                if (lineCount == 1) return@forEach
                
                val parsed = parseCsvLine(line)
                if (parsed != null) {
                    words.add(
                        Word(
                            englishWord = parsed.first,
                            russianTranslations = parsed.second
                        )
                    )
                }
            }
        }
        
        // Получить существующие английские слова из базы
        val existingEnglishWords = repository.getAllEnglishWords().toSet()
        
        // Фильтровать только новые слова
        val newWords = words.filter { word -> word.englishWord !in existingEnglishWords }
        
        // Сохранить только новые слова в базу данных
        if (newWords.isNotEmpty()) {
            repository.insertAll(newWords)
        }
        
        return@withContext newWords.size
    }
    
    private fun parseCsvLine(line: String): Pair<String, String>? {
        if (line.isBlank()) return null
        
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
        
        return if (parts.size >= 2) {
            Pair(parts[0], parts[1])
        } else null
    }
    
    suspend fun isDataImported(): Boolean {
        return repository.getTotalCount() > 0
    }
}
