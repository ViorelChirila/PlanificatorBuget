package com.example.planificatorbuget.utils

import com.example.planificatorbuget.data.forecastHoltLinear
import com.example.planificatorbuget.model.TransactionModel
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date


enum class PredictionModel {
    AR,
    ARMA,
    HoltLinear
}
fun predictFutureBudgetHoltLinear(
    transactions: List<TransactionModel>,
    alpha: Double,
    beta: Double,
    intervalsAhead: Int,
    initialBudget: Double
): List<Pair<Date, Double>> {
    val budgetHistory = calculateCumulativeBudget(transactions, initialBudget)
    val budgetValues = budgetHistory.map { it.second }

    val futureBudgets = mutableListOf<Pair<Date, Double>>()
    val forecastedValues = forecastHoltLinear(budgetValues, alpha, beta, intervalsAhead)

    val calendar = Calendar.getInstance()
    calendar.time = budgetHistory.last().first

    for (i in forecastedValues.indices) {
        calendar.add(Calendar.MONTH, 1)
        futureBudgets.add(calendar.time to forecastedValues[i])
    }

    return budgetHistory + futureBudgets
}
fun calculateCumulativeBudget(
    transactions: List<TransactionModel>,
    initialBudget: Double
): List<Pair<Date, Double>> {
    val consolidatedTransactions = getConsolidatedTransactions(transactions)
    var cumulativeBudget = initialBudget
    return consolidatedTransactions.map { (date, amount) ->
        cumulativeBudget += amount
        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()) to cumulativeBudget
    }
}

fun getConsolidatedTransactions(transactions: List<TransactionModel>): List<Pair<LocalDate, Double>> {
    val consolidatedTransactions = transactions.groupBy { transaction ->
        transaction.transactionDate.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.mapValues { entry ->
        entry.value.sumOf { it.amount }
    }.toList().sortedBy { it.first }

    return consolidatedTransactions
}


fun calculateAR(data: List<Double>, order: Int): Pair<DoubleArray, Double>? {
    if (data.size <= order) {
        // Not enough data to fit the model
        return null
    }

    val n = data.size
    val x = Array(n - order) { DoubleArray(order) }
    val y = DoubleArray(n - order)

    for (i in order until n) {
        for (j in 0 until order) {
            x[i - order][j] = data[i - j - 1]
        }
        y[i - order] = data[i]
    }

    val regression = OLSMultipleLinearRegression()
    regression.newSampleData(y, x)
    val coefficients = regression.estimateRegressionParameters()

    return Pair(coefficients, regression.estimateRegressionStandardError())
}

fun predictFutureBudgetAR(
    transactions: List<TransactionModel>,
    order: Int,
    intervalsAhead: Int,
    initialBudget: Double
): List<Pair<Date, Double>> {
    val budgetHistory = calculateCumulativeBudget(transactions, initialBudget)
    val budgetValues = budgetHistory.map { it.second }
    val arResult = calculateAR(budgetValues, order)
        ?: // Not enough data to predict
        return budgetHistory

    val (coefficients, _) = arResult

    val futureBudgets = mutableListOf<Pair<Date, Double>>()
    var predictedValue: Double
    val recentData = budgetValues.takeLast(order).toMutableList()

    val calendar = Calendar.getInstance()
    calendar.time = budgetHistory.last().first

    for (i in 1..intervalsAhead) {
        calendar.add(Calendar.MONTH, 1)

        predictedValue =
            coefficients[0] + coefficients.drop(1).zip(recentData) { coef, value -> coef * value }
                .sum()
        futureBudgets.add(calendar.time to predictedValue)
        recentData.add(predictedValue)
        if (recentData.size > order) {
            recentData.removeAt(0)
        }
    }

    return budgetHistory + futureBudgets
}



fun calculateARMA(data: List<Double>, p: Int, q: Int): Triple<DoubleArray, DoubleArray, Double> {
    val n = data.size
    val x = Array(n - p) { DoubleArray(p) }
    val y = DoubleArray(n - p)
    val errors = DoubleArray(n - p)

    for (i in p until n) {
        for (j in 0 until p) {
            x[i - p][j] = data[i - j - 1]
        }
        y[i - p] = data[i]
    }

    val regression = OLSMultipleLinearRegression()
    regression.newSampleData(y, x)
    val arCoefficients = regression.estimateRegressionParameters()

    for (i in p until n) {
        var error = y[i - p] - arCoefficients[0]
        for (j in 1..p) {
            error -= arCoefficients[j] * data[i - j]
        }
        errors[i - p] = error
    }

    val xErrors = Array(n - p - q) { DoubleArray(q) }
    val yErrors = DoubleArray(n - p - q)

    for (i in q until n - p) {
        for (j in 0 until q) {
            xErrors[i - q][j] = errors[i - j - 1]
        }
        yErrors[i - q] = errors[i]
    }

    regression.newSampleData(yErrors, xErrors)
    val maCoefficients = regression.estimateRegressionParameters()

    return Triple(arCoefficients, maCoefficients, regression.estimateRegressionStandardError())
}

fun predictFutureBudgetARMA(
    transactions: List<TransactionModel>,
    p: Int,
    q: Int,
    intervalsAhead: Int,
    initialBudget: Double
): List<Pair<Date, Double>> {
    val budgetHistory = calculateCumulativeBudget(transactions, initialBudget)
    val budgetValues = budgetHistory.map { it.second }
    val (arCoefficients, maCoefficients, _) = calculateARMA(budgetValues, p, q)

    val futureBudgets = mutableListOf<Pair<Date, Double>>()
    var predictedValue: Double
    val recentData = budgetValues.takeLast(p).toMutableList()
    val recentErrors = MutableList(q) { 0.0 }

    val calendar = Calendar.getInstance()
    calendar.time = budgetHistory.last().first

    for (i in 1..intervalsAhead) {
        calendar.add(Calendar.MONTH, 1)

        predictedValue = arCoefficients[0] + arCoefficients.drop(1)
            .zip(recentData) { coef, value -> coef * value }.sum()
        if (recentErrors.size >= q) {
            predictedValue += maCoefficients.drop(1).zip(recentErrors) { coef, err -> coef * err }
                .sum()
        }

        futureBudgets.add(calendar.time to predictedValue)

        recentData.add(predictedValue)
        if (recentData.size > p) {
            recentData.removeAt(0)
        }

        recentErrors.add(
            predictedValue - (arCoefficients[0] + arCoefficients.drop(1)
                .zip(recentData) { coef, value -> coef * value }.sum())
        )
        if (recentErrors.size > q) {
            recentErrors.removeAt(0)
        }
    }

    return budgetHistory + futureBudgets
}

