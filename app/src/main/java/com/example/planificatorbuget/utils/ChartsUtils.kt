package com.example.planificatorbuget.utils

import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.planificatorbuget.model.TransactionCategoriesModel
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

enum class SamplingPeriod {
    DAILY,
    WEEKLY,
    MONTHLY
}



fun getTransactionsForDateRange(transactions: List<TransactionModel>, days: Int): List<TransactionModel> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    val startDate = calendar.time

    return transactions.filter {
        it.transactionDate.toDate().after(startDate)
    }
}

fun getTransactionsForType(transactions: List<TransactionModel>, type: String): List<TransactionModel> {
    return transactions.filter {
        it.transactionType.equals(type, ignoreCase = true)
    }
}

fun calculateCategoryTotals(transactions: List<TransactionModel>, categories: List<TransactionCategoriesModel>): Map<String, Float> {
    val categoryTotals = mutableMapOf<String, Float>()

    transactions.forEach { transaction ->
        val categoryName = categories.find { it.categoryId == transaction.categoryId }?.categoryName ?: "Unknown"
        val amount = if(transaction.amount.toFloat()>0) transaction.amount.toFloat() else transaction.amount.toFloat()*(-1)
        categoryTotals[categoryName] = categoryTotals.getOrDefault(categoryName, 0f) + amount
    }

    return categoryTotals
}

fun createPieChartData(categoryTotals: Map<String, Float>): List<PieChartData.Slice> {
    val colors = listOf(Color(0xFF333333), Color(0xFF666a86), Color(0xFF95B8D1), Color(0xFFF53844))
    var colorIndex = 0

    val slices = categoryTotals.map { (category, total) ->
        val color = colors[colorIndex % colors.size]
        colorIndex++
        PieChartData.Slice(category, total, color)
    }

    return slices
}


fun getCurrentAndPreviousMonth(): Pair<String, String> {
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale("ro","RO"))
    val calendar = Calendar.getInstance()

    val currentMonth = dateFormat.format(calendar.time)
    calendar.add(Calendar.MONTH, -1)
    val previousMonth = dateFormat.format(calendar.time)

    return currentMonth to previousMonth
}

fun getRecentTwoMonthsData(groupBars: List<GroupBar>): List<Pair<String, Pair<Double, Double>>> {
    val (currentMonth, previousMonth) = getCurrentAndPreviousMonth()

    return groupBars.filter { it.label == currentMonth || it.label == previousMonth }
        .map { groupBar ->
            val expenses = groupBar.barList.find { it.label == "Cheltuieli" }?.point?.y ?: 0.0
            val incomes = groupBar.barList.find { it.label == "Venituri" }?.point?.y ?: 0.0
            groupBar.label to (expenses.toDouble() to incomes.toDouble())
        }
}
fun prepareDataForGroupedBarChart(
    transactions: List<TransactionModel>,
    period: SamplingPeriod,
    periodsCount: Int
): List<GroupBar> {
    val endDate = getEndOfDay(Date())
    val startDate = when (period) {
        SamplingPeriod.DAILY -> getDateDaysAgo(periodsCount - 1)
        SamplingPeriod.WEEKLY -> getDateWeeksAgo(periodsCount - 1)
        SamplingPeriod.MONTHLY -> getDateMonthsAgo(periodsCount - 1)
    }

    val allPeriodStartDates = generateDateRangeWithPeriodSelection(startDate, endDate, period)

    return allPeriodStartDates.mapIndexed { index, periodStartDate ->
        val periodEndDate = getEndOfPeriod(periodStartDate, period)
        val filteredTransactions = transactions.filter {
            it.transactionDate.toDate().time >= periodStartDate.time &&
                    it.transactionDate.toDate().time <= periodEndDate.time
        }

        val totalExpenses = filteredTransactions.filter { it.transactionType == "Venit" }
            .sumOf { it.amount }
        val totalIncomes = filteredTransactions.filter { it.transactionType == "Cheltuiala" }
            .sumOf { it.amount*(-1) }

        val periodString = when (period) {
            SamplingPeriod.DAILY -> SimpleDateFormat("MMM dd, yyyy", Locale("ro","RO")).format(periodStartDate)
            SamplingPeriod.WEEKLY -> SimpleDateFormat("MMM dd", Locale("ro","RO")).format(periodStartDate) + " - " + SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(periodEndDate)
            SamplingPeriod.MONTHLY -> SimpleDateFormat("MMM yyyy", Locale("ro","RO")).format(periodStartDate)
        }

        val bars = listOf(
            BarData(Point(index.toFloat(), totalExpenses.toFloat()), label = "Venituri", description = "Total expenses for $periodString"),
            BarData(Point(index.toFloat(), totalIncomes.toFloat()), label = "Cheltuieli", description = "Total incomes for $periodString")
        )

        GroupBar(periodString, bars)
    }
}

fun getEndOfPeriod(date: Date, period: SamplingPeriod): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    when (period) {
        SamplingPeriod.DAILY -> {
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
        }
        SamplingPeriod.WEEKLY -> {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
        }
        SamplingPeriod.MONTHLY -> {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
        }
    }
    return calendar.time
}
fun getDateWeeksAgo(weeks: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, -weeks)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}
fun getDateMonthsAgo(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -months)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun generateDateRangeWithPeriodSelection(startDate: Date, endDate: Date, period: SamplingPeriod): List<Date> {
    val dates = mutableListOf<Date>()
    val calendar = Calendar.getInstance()
    calendar.time = startDate

    while (!calendar.time.after(endDate)) {
        dates.add(calendar.time.clone() as Date)
        when (period) {
            SamplingPeriod.DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            SamplingPeriod.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            SamplingPeriod.MONTHLY -> calendar.add(Calendar.MONTH, 1)
        }
    }

    return dates
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