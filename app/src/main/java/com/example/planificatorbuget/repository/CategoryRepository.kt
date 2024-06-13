package com.example.planificatorbuget.repository

import android.util.Log
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.TransactionCategoriesModel
import com.example.planificatorbuget.model.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepository @Inject constructor(private val firebaseFirestore: FirebaseFirestore, private val auth: FirebaseAuth) {

    companion object {
        private const val TAG = "CategoryRepository"
        private const val CATEGORIES_COLLECTION = "categories"
    }
    suspend fun fetchCategories(): DataOrException<List<TransactionCategoriesModel>, Boolean, Exception> {
        val dataOrExceptionCategory = DataOrException<List<TransactionCategoriesModel>, Boolean, Exception>()
        val userId = auth.currentUser?.uid ?: return dataOrExceptionCategory.apply {
            isLoading = false
        }
        dataOrExceptionCategory.isLoading = true
        return try {
            val querySnapshot =
                firebaseFirestore.collection(CATEGORIES_COLLECTION)
                    .whereEqualTo("user_id", userId).get().await()
            val transactions = querySnapshot.toObjects(TransactionCategoriesModel::class.java)
            dataOrExceptionCategory.data = transactions
            Log.d(TAG, "fetchTransactions: ${transactions.size}")
            dataOrExceptionCategory.isLoading = false
            dataOrExceptionCategory
        } catch (e: Exception) {
            dataOrExceptionCategory.exception = e
            dataOrExceptionCategory.isLoading = false
            dataOrExceptionCategory
        }
    }

    suspend fun addCategory(category: TransactionCategoriesModel): Response<Boolean> {
        val userId = auth.currentUser?.uid ?: return Response.Error("User not authenticated")
        return try {
            firebaseFirestore.collection(CATEGORIES_COLLECTION).add(category.apply {
                this.userId = userId
            }).addOnSuccessListener { documentReference ->
                val docId = documentReference.id
                firebaseFirestore.collection(CATEGORIES_COLLECTION).document(docId).update("category_id", docId)
            }.await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }

    suspend fun fetchCategoryById(categoryId: String): TransactionCategoriesModel? {
        return try {
            val result = firebaseFirestore.collection(CATEGORIES_COLLECTION).document(categoryId).get().await()
            result.toObject(TransactionCategoriesModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}