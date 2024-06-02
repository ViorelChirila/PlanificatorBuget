package com.example.planificatorbuget.repository

import android.util.Log
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "TransactionsRepository"
        private const val TRANSACTIONS_COLLECTION = "transactions"
    }

    suspend fun fetchTransactions(): DataOrException<List<TransactionModel>, Boolean, Exception> {
        val dataOrExceptionTransactions = DataOrException<List<TransactionModel>, Boolean, Exception>()
        val userId = auth.currentUser?.uid ?: return dataOrExceptionTransactions.apply {
            isLoading = false
        }

        dataOrExceptionTransactions.isLoading = true
        return try {
            val querySnapshot = firebaseFirestore.collection(TRANSACTIONS_COLLECTION).whereEqualTo("user_id", userId).get().await()
            val transactions = querySnapshot.toObjects(TransactionModel::class.java)
            dataOrExceptionTransactions.data = transactions
            Log.d(TAG, "fetchTransactions: ${transactions.size}")
            dataOrExceptionTransactions.isLoading = false
            dataOrExceptionTransactions
        } catch (e: Exception) {
            dataOrExceptionTransactions.exception = e
            dataOrExceptionTransactions.isLoading = false
            dataOrExceptionTransactions
        }
    }

    suspend fun addTransaction(transaction: TransactionModel): Response<Boolean> {
        val userId = auth.currentUser?.uid ?: return Response.Error("User not authenticated")

        return try {
            firebaseFirestore.collection(TRANSACTIONS_COLLECTION).add(transaction.apply {
                this.userId = userId
            }).addOnSuccessListener {documentReference ->
               val docId = documentReference.id
                firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(docId).update("transaction_id", docId)
            }.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }
}