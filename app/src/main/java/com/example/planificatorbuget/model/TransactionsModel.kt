package com.example.planificatorbuget.model

class TransactionsModel(
    var transactionId: String = "",
    var userId: String = "",
    var amount: Double = 0.0,
    var transactionType: String = "",
    var categoryId: String = "",
    var transactionDate: String = "",
    var transactionDescription: String = ""
) {
}