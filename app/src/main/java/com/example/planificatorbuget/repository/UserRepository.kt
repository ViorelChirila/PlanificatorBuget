package com.example.planificatorbuget.repository

import android.util.Log
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebaseFirestore: FirebaseFirestore) {
    suspend fun fetchUser(): DataOrException<UserModel,Boolean,Exception> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dataOrException = DataOrException<UserModel,Boolean,Exception>()

        try {
            dataOrException.isLoading = true
            if(userId!= null){
                val documentSnapshot = firebaseFirestore.collection("users").document(userId).get().await()
                val user = documentSnapshot.toObject(UserModel::class.java)
                if (user != null) {
                    dataOrException.data = user
                }
                dataOrException.isLoading = false
            }
        }catch (e: Exception){
            dataOrException.exception = e
            dataOrException.isLoading = false
        }
        return dataOrException
    }

}