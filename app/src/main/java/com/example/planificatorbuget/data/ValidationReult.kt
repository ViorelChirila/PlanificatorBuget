package com.example.planificatorbuget.data

import android.util.Log
import com.example.planificatorbuget.model.TransactionModel
import com.example.planificatorbuget.utils.formatDateTimeStringToTimestamp
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.text.SimpleDateFormat
import java.util.Locale

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