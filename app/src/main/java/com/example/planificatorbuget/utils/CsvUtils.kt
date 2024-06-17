package com.example.planificatorbuget.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.OutputStream

fun exportTransactionsToCsv(context: Context,transactions: List<Pair<TransactionModel,String>>,fileName: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    uri?.let {
        try {
            resolver.openOutputStream(it)?.use { outputStream ->
                writeCsvToStream(outputStream, transactions)
            }
            Log.d("CSV Export", "CSV file created successfully at $uri")
        } catch (e: Exception) {
            Log.e("CSV Export", "Error creating CSV file", e)
            return null
        }
    }
    return uri
}

private fun writeCsvToStream(outputStream: OutputStream, transactions: List<Pair<TransactionModel,String>>) {
    csvWriter().open(outputStream) {
        // Write the header
        writeRow(
            listOf(
                "Titlul tranzactiei",
                "Suma",
                "Tipul tranzactiei",
                "Data",
                "Categoria",
                "Descriere"
            )
        )

        // Write the transactions
        transactions.forEach { transaction ->
            writeRow(
                listOf(
                    transaction.first.transactionTitle,
                    transaction.first.amount.toString(),
                    transaction.first.transactionType,
                    formatTimestampToString(transaction.first.transactionDate),
                    transaction.second,
                    transaction.first.transactionDescription
                )
            )
        }
    }
}
fun mapTransactionsToCategories(
    transactions: List<TransactionModel>,
    categories: List<TransactionCategoriesModel>
): List<Pair<TransactionModel, String>> {
    val categoryMap = categories.associateBy { it.categoryId }
    val result = mutableListOf<Pair<TransactionModel, String>>()

    transactions.forEach { transaction ->
        val categoryName = categoryMap[transaction.categoryId]?.categoryName ?: "Unknown Category"
        result.add(Pair(transaction, categoryName))
    }

    return result
}

fun validateCsvContent(content: String): Pair<Boolean, List<TransactionModel>> {
    val reader = csvReader()
    val rows = try {
        reader.readAll(content)
    } catch (e: Exception) {
        throw IllegalArgumentException("Error parsing CSV: ${e.message}")
    }

    if (rows.isEmpty() || rows[0].size < 6) {
        return Pair(false, emptyList())
    }

    val header = rows[0]
    if (header[0] != "Titlul tranzactiei" ||
        header[1] != "Suma" ||
        header[2] != "Tipul tranzactiei" ||
        header[3] != "Data" ||
        header[4] != "Ora" ||
        header[5] != "Descriere"
    ) {
        return Pair(false, emptyList())
    }

    val transactions = mutableListOf<TransactionModel>()
    for (i in 1 until rows.size) {
        val row = rows[i]
        try {
            val amount = row[1].toDoubleOrNull()
                ?: throw IllegalArgumentException("Valoare invalida pentru campul \"Valoare\" in randul $i: not a double value")

            val transactionType = row[2]
            if (transactionType != "Venit" && transactionType != "Cheltuiala") {
                throw IllegalArgumentException("Tip invalid in randul $i")
            }

            if (transactionType == "Venit" && amount < 0) {
                throw IllegalArgumentException("Valoare invalida pentru campul \"Valoare\" in randul $i: venitul nu poate fi negativ")
            }
            if (transactionType == "Cheltuiala" && amount > 0) {
                throw IllegalArgumentException("Valoare invalida pentru campul \"Valoare\" in randul $i: cheltuiala nu poate fi pozitiva")
            }

            val dateStr = row[3]
            Log.d("CsvViewModel", "validateCsvContent: dateStr: $dateStr")
            val timeStr = row[4]
            Log.d("CsvViewModel", "validateCsvContent: timeStr: $timeStr")
            val description = row[5]

            val dateTimeStr = "${row[3]} ${row[4]}"
            val transactionDate = formatDateTimeStringToTimestamp(dateTimeStr)
                ?: throw IllegalArgumentException("Format invalid al datei sau orei $i")

            val transaction = TransactionModel(
                amount = amount,
                transactionType = transactionType,
                transactionDate = transactionDate,
                transactionTitle = row[0],
                transactionDescription = description,
            )
            transactions.add(transaction)
        } catch (e: Exception) {
            throw IllegalArgumentException("Format invalid al fisierului  ${e.message}")
        }
    }
    return Pair(true, transactions)
}