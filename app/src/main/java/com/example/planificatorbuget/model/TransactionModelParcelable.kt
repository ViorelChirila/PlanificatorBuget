package com.example.planificatorbuget.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionModelParcelable(
    var transactionId: String? = null,
    var userId: String? = "",
    var amount: Double = 0.0,
    var transactionType: String = "",
    var categoryId: String? = null,
    var transactionDate: Timestamp = Timestamp.now(),
    var transactionTitle: String = "",
    var transactionDescription: String = "",
    var budgetSnapshot: Double = 0.0
) : Parcelable{
    companion object {
        fun fromTransactionModel(model: TransactionModel) = TransactionModelParcelable(
            transactionId = model.transactionId,
            userId = model.userId,
            amount = model.amount,
            transactionType = model.transactionType,
            categoryId = model.categoryId,
            transactionDate = model.transactionDate,
            transactionTitle = model.transactionTitle,
            transactionDescription = model.transactionDescription,
            budgetSnapshot = model.budgetSnapshot
        )
        fun fromTransactionModelList(models: List<TransactionModel>): List<TransactionModelParcelable> {
            return models.map { fromTransactionModel(it) }
        }
        fun toTransactionModel(parcelables: List<TransactionModelParcelable>): List<TransactionModel> {
            return parcelables.map { toTransactionModel(it) }
        }

        private fun toTransactionModel(parcelable: TransactionModelParcelable) = TransactionModel(
            transactionId = parcelable.transactionId,
            userId = parcelable.userId,
            amount = parcelable.amount,
            transactionType = parcelable.transactionType,
            categoryId = parcelable.categoryId,
            transactionDate = parcelable.transactionDate,
            transactionTitle = parcelable.transactionTitle,
            transactionDescription = parcelable.transactionDescription,
            budgetSnapshot = parcelable.budgetSnapshot
        )
    }
}