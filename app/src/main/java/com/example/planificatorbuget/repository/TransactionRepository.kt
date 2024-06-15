package com.example.planificatorbuget.repository

import android.util.Log
import androidx.core.net.toUri
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
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
        val dataOrExceptionTransactions =
            DataOrException<List<TransactionModel>, Boolean, Exception>()
        val userId = auth.currentUser?.uid ?: return dataOrExceptionTransactions.apply {
            isLoading = false
        }

        dataOrExceptionTransactions.isLoading = true
        return try {
            val querySnapshot = firebaseFirestore.collection(TRANSACTIONS_COLLECTION)
                .whereEqualTo("user_id", userId).get().await()
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
            val latestTransaction = firebaseFirestore.collection(TRANSACTIONS_COLLECTION)
                .orderBy("transaction_date", Query.Direction.DESCENDING)
                .limit(1).get().await().documents.firstOrNull()
                ?.toObject(TransactionModel::class.java)

            if (!(latestTransaction != null && transaction.transactionDate < latestTransaction.transactionDate)) {
                val docRef =
                    firebaseFirestore.collection(TRANSACTIONS_COLLECTION).add(transaction.apply {
                        this.userId = userId
                    }).addOnSuccessListener { documentReference ->
                        val docId = documentReference.id
                        firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(docId)
                            .update("transaction_id", docId)
                    }.await()
                if (transaction.descriptionImage != "null") {
                    addDescriptionImageToFirestore(docRef, transaction)
                }
                Response.Success(true)
                Log.d(TAG, "addTransaction: Success")
            } else {
                val subsequentTransactions = firebaseFirestore.collection(TRANSACTIONS_COLLECTION)
                    .whereGreaterThan("transaction_date", transaction.transactionDate)
                    .orderBy("transaction_date", Query.Direction.ASCENDING)
                    .get().await()
                    .toObjects(TransactionModel::class.java)

                transaction.budgetSnapshot =
                    subsequentTransactions.firstOrNull()?.budgetSnapshot ?: 0.0

                for (subsequentTransaction in subsequentTransactions) {
                    subsequentTransaction.budgetSnapshot += transaction.amount
                    firebaseFirestore.collection(TRANSACTIONS_COLLECTION)
                        .document(subsequentTransaction.transactionId!!)
                        .update("budget_snapshot", subsequentTransaction.budgetSnapshot)
                        .await()
                }
                val docRef =firebaseFirestore.collection(TRANSACTIONS_COLLECTION).add(transaction.apply {
                    this.userId = userId
                }).addOnSuccessListener { documentReference ->
                    val docId = documentReference.id
                    firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(docId)
                        .update("transaction_id", docId)
                }.await()
                if (transaction.descriptionImage != "null") {
                    addDescriptionImageToFirestore(docRef, transaction)
                }
                Response.Success(true)
                Log.d(TAG, "addTransactionOld: Success")
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }

    private suspend fun addDescriptionImageToFirestore(
        docRef: DocumentReference,
        transaction: TransactionModel
    ) {
        val imageRef =
            FirebaseStorage.getInstance().reference.child("transaction_images/${docRef.id}")
        imageRef.putFile(transaction.descriptionImage!!.toUri()).await()

        val downloadUrl = imageRef.downloadUrl.await().toString()

        firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(docRef.id)
            .update("description_image", downloadUrl).await()
    }

    suspend fun fetchTransactionById(transactionId: String): DataOrException<TransactionModel, Boolean, Exception> {
        val dataOrExceptionTransaction = DataOrException<TransactionModel, Boolean, Exception>()
        dataOrExceptionTransaction.isLoading = true
        return try {
            val querySnapshot = firebaseFirestore.collection(TRANSACTIONS_COLLECTION)
                .document(transactionId).get().await()
            val transaction = querySnapshot.toObject(TransactionModel::class.java)
            dataOrExceptionTransaction.data = transaction!!
            dataOrExceptionTransaction.isLoading = false
            dataOrExceptionTransaction
        } catch (e: Exception) {
            dataOrExceptionTransaction.exception = e
            dataOrExceptionTransaction.isLoading = false
            dataOrExceptionTransaction
        }
    }

    suspend fun updateTransactionDescription(transactionId: String, newDescription: String): Response<Boolean> {
        return try {
            firebaseFirestore.collection(TRANSACTIONS_COLLECTION).document(transactionId)
                .update("transaction_description", newDescription).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }
}