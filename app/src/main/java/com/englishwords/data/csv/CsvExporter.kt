package com.englishwords.data.csv

import com.englishwords.data.local.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class CsvExporter {
    
    suspend fun exportWordsToCsv(words: List<Word>, outputStream: OutputStream): Int = withContext(Dispatchers.IO) {
        val writer = outputStream.bufferedWriter()
        
        // Записать заголовок
        writer.write("english,russian,repetitionLevel,lastReviewDate,nextReviewDate,correctCount,incorrectCount,consecutiveCorrect,createdAt\n")
        
        // Записать каждое слово
        words.forEach { word ->
            val line = buildCsvLine(word)
            writer.write(line)
            writer.write("\n")
        }
        
        writer.flush()
        
        return@withContext words.size
    }
    
    private fun buildCsvLine(word: Word): String {
        return listOf(
            escapeCsvField(word.englishWord),
            escapeCsvField(word.russianTranslations),
            word.repetitionLevel.toString(),
            word.lastReviewDate?.toString() ?: "",
            word.nextReviewDate?.toString() ?: "",
            word.correctCount.toString(),
            word.incorrectCount.toString(),
            word.consecutiveCorrect.toString(),
            word.createdAt.toString()
        ).joinToString(",")
    }
    
    private fun escapeCsvField(field: String): String {
        return if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
}
