package com.example.planificatorbuget.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTimestampToString(timestamp: Timestamp, pattern: String = "MM/dd/yyyy"): String {
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(date)
}

fun formatStringToTimestamp(dateString: String, pattern: String = "MM/dd/yyyy"): Timestamp? {
    return try {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val date: Date = dateFormat.parse(dateString)!!
        Timestamp(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun isDateInPast(dateString: String, pattern: String = "MM/dd/yyyy"): Boolean {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val date = dateFormat.parse(dateString)
    val currentDate = dateFormat.parse(dateFormat.format(Date()))
    return date?.before(currentDate) ?: false
}

fun isDateBeforeToday(dateString: String, pattern: String = "MM/dd/yyyy"): Boolean {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.before(Date()) ?: false
}