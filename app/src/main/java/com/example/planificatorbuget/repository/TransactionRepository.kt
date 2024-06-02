package com.example.planificatorbuget.repository

import com.example.planificatorbuget.data.DataOrException
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
            val querySnapshot = firebaseFirestore.collection(TRANSACTIONS_COLLECTION).whereEqualTo("userId", userId).get().await()
            val transactions = querySnapshot.toObjects(TransactionModel::class.java)
            dataOrExceptionTransactions.data = transactions
            dataOrExceptionTransactions.isLoading = false
            dataOrExceptionTransactions
        } catch (e: Exception) {
            dataOrExceptionTransactions.exception = e
            dataOrExceptionTransactions.isLoading = false
            dataOrExceptionTransactions
        }
    }

    suspend fun addTransaction(transaction: TransactionModel): DataOrException<Boolean, Boolean, Exception> {
        val dataOrException = DataOrException<Boolean, Boolean, Exception>()
        val userId = auth.currentUser?.uid ?: return dataOrException.apply {
            isLoading = false
        }

        dataOrException.isLoading = true
        return try {
            firebaseFirestore.collection(TRANSACTIONS_COLLECTION).add(transaction.apply {
                this.userId = userId
            }).addOnSuccessListener {documentReference ->
               val docId = documentReference.id
                firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(docId).update("transaction_id", docId)
            }.await()
            dataOrException.data = true
            dataOrException.isLoading = false
            dataOrException
        } catch (e: Exception) {
            dataOrException.exception = e
            dataOrException.isLoading = false
            dataOrException
        }
    }
}