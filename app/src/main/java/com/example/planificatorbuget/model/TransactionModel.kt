package com.example.planificatorbuget.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

class TransactionModel(
    @get: PropertyName("transaction_id")
    var transactionId: String? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = "",

    @get:PropertyName("amount")
    @set:PropertyName("amount")
    var amount: Double = 0.0,

    @get:PropertyName("transaction_type")
    @set:PropertyName("transaction_type")
    var transactionType: String = "",

    @get:PropertyName("category_id")
    @set:PropertyName("category_id")
    var categoryId: String? = null,

    @get:PropertyName("transaction_date")
    @set:PropertyName("transaction_date")
    var transactionDate: String = "",

    @get:PropertyName("transaction_title")
    @set:PropertyName("transaction_title")
    var transactionTitle: String = "",

    @get:PropertyName("transaction_description")
    @set:PropertyName("transaction_description")
    var transactionDescription: String = ""
) {
}