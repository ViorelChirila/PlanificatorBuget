package com.example.planificatorbuget.data

data class HoltLinearResult(val level: Double, val trend: Double)

fun holtLinearSmoothing(
    data: List<Double>,
    alpha: Double,
    beta: Double
): List<HoltLinearResult> {
    val results = mutableListOf<HoltLinearResult>()
    if (data.size < 2) return results

    var level = data[0]
    var trend = data[1] - data[0]

    results.add(HoltLinearResult(level, trend))

    for (i in 1 until data.size) {
        val newLevel = alpha * data[i] + (1 - alpha) * (level + trend)
        val newTrend = beta * (newLevel - level) + (1 - beta) * trend
        level = newLevel
        trend = newTrend
        results.add(HoltLinearResult(level, trend))
    }

    return results
}

fun forecastHoltLinear(
    data: List<Double>,
    alpha: Double,
    beta: Double,
    intervalsAhead: Int
): List<Double> {
    val results = holtLinearSmoothing(data, alpha, beta)
    if (results.isEmpty()) return emptyList()

    val lastResult = results.last()
    val level = lastResult.level
    val trend = lastResult.trend

    return List(intervalsAhead) { level + trend * (it + 1) }
}

