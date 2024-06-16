package com.example.planificatorbuget.repository

import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.RecurringTransactionModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecurringTransactionRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "RecurringTransactionRepository"
        private const val RECURRING_TRANSACTIONS_COLLECTION = "recurring_transactions"
    }

    suspend fun fetchRecurringTransactions(): List<RecurringTransactionModel> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val result = firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).whereEqualTo("user_Id",userId).get().await()
            result.toObjects(RecurringTransactionModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addRecurringTransaction(recurringTransaction: RecurringTransactionModel): Response<Boolean> {
        val userId = auth.currentUser?.uid ?: return Response.Error("User not authenticated")

        return try {
            firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).add(recurringTransaction.apply {
                this.userId = userId
            }).addOnSuccessListener {documentReference ->
                val docId = documentReference.id
                firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).document(docId).update("transaction_id", docId)
            }.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }

    suspend fun updateRecurringTransaction(
        recurringTransactionId: String,
        startDate: Timestamp,
        endDate: Timestamp,
        recurrenceInterval: String
    ): Response<Boolean> {
        return try {
            firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).document(recurringTransactionId).update(
                mapOf(
                    "start_date" to startDate,
                    "end_date" to endDate,
                    "recurrence_interval" to recurrenceInterval
                )
            ).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }

    suspend fun deleteRecurringTransaction(transactionId: String): Response<Boolean> {
        return try {
            firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).document(transactionId).delete().await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }

    suspend fun updateRecurrentTransactionStatus(
        transactionId: String,
        status: String
    ): Response<Boolean> {
        return try {
            firebaseFirestore.collection(RECURRING_TRANSACTIONS_COLLECTION).document(transactionId).update("status", status).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }
}