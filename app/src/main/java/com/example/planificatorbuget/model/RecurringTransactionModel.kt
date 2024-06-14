package com.example.planificatorbuget.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

class RecurringTransactionModel(
    @get: PropertyName("transaction_id")
    @set: PropertyName("transaction_id")
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

    @get:PropertyName("transaction_title")
    @set:PropertyName("transaction_title")
    var transactionTitle: String = "",

    @get:PropertyName("transaction_description")
    @set:PropertyName("transaction_description")
    var transactionDescription: String = "",

    @get:PropertyName("start_date")
    @set:PropertyName("start_date")
    var startDate: Timestamp = Timestamp.now(),

    @get:PropertyName("end_date")
    @set:PropertyName("end_date")
    var endDate: Timestamp = Timestamp.now(),

    @get:PropertyName("recurrence_interval")
    @set:PropertyName("recurrence_interval")
    var recurrenceInterval: String = "",

    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "activ",

    @get:PropertyName("description_image")
    @set:PropertyName("description_image")
    var descriptionImageUri: String? = ""
) {

}