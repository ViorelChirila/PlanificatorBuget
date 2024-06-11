package com.example.planificatorbuget.utils

fun calculateMean(expensesData: List<Pair<String, Double>>): Double {
    val totalAmount = expensesData.sumOf { it.second }
    return if (expensesData.isNotEmpty()) totalAmount / expensesData.size else 0.0
}