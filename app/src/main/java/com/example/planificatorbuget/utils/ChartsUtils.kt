package com.example.planificatorbuget.utils

import com.example.planificatorbuget.model.TransactionModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

enum class Period {
    LAST_7_DAYS,
    LAST_MONTH,
    LAST_3_MONTHS
}
fun filterTransactionsByPeriod(
    transactions: List<TransactionModel>,
    period: Period,
    transactionType: String,
    showAsDayName: Boolean = true
): List<Pair<String, Double>> {
    val endDate = getEndOfDay(Date())
    val startDate = when (period) {
        Period.LAST_7_DAYS -> getDateDaysAgo(6)  // 6 days ago + today = 7 days
        Period.LAST_MONTH -> getDateDaysAgo(29)  // 29 days ago + today = 30 days
        Period.LAST_3_MONTHS -> getDateDaysAgo(89)  // 89 days ago + today = 90 days
    }

    val allDates = generateDateRange(startDate, endDate)

    val filteredTransactions = transactions.filter {
        it.transactionDate.toDate().time >= startDate.time &&
                it.transactionDate.toDate().time <= endDate.time &&
                it.transactionType == transactionType
    }

    val transactionMap = filteredTransactions.groupBy { it.transactionDate.toDate().toSimpleDateString() }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    return allDates.map { date ->
        val dateString = if (showAsDayName) date.toDayOfWeekString(Locale("ro","RO")) else date.toSimpleDateString()
        dateString to (transactionMap[date.toSimpleDateString()] ?: 0.0)
    }
}


fun generateDateRange(startDate: Date, endDate: Date): List<Date> {
    val dates = mutableListOf<Date>()
    val calendar = Calendar.getInstance()
    calendar.time = startDate

    while (!calendar.time.after(endDate)) {
        dates.add(calendar.time.clone() as Date)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return dates
}

fun getDateDaysAgo(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun getEndOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.time
}

fun Date.toSimpleDateString(): String {
    val format = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return format.format(this)
}

fun calculateYAxisSteps(maxAmount: Double, desiredStepCount: Int): List<String> {
    val stepSize = ceil(maxAmount / desiredStepCount).toFloat()
    return (0..desiredStepCount).map { (it * stepSize).toInt().toString() }
}

fun Date.toDayOfWeekString(locale: Locale = Locale.getDefault()): String {
    val format = SimpleDateFormat("EEE", locale)
    return format.format(this)
}