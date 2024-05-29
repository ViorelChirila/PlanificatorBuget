package com.example.planificatorbuget.repository

import android.net.Uri
import android.util.Log
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val firebaseFirestore: FirebaseFirestore,private val auth: FirebaseAuth) {
    suspend fun fetchUser(): DataOrException<UserModel, Boolean, Exception> {
        val userId = auth.currentUser?.uid
        val dataOrException = DataOrException<UserModel, Boolean, Exception>()

        try {
            dataOrException.isLoading = true
            if (userId != null) {
                val documentSnapshot =
                    firebaseFirestore.collection("users").document(userId).get().await()
                val user = documentSnapshot.toObject(UserModel::class.java)
                if (user != null) {
                    dataOrException.data = user
                }
                dataOrException.isLoading = false
            }
        } catch (e: Exception) {
            dataOrException.exception = e
            dataOrException.isLoading = false
        }
        return dataOrException
    }

    suspend fun updateUserData(user: Map<String,Any>): DataOrException<Boolean, Boolean, Exception> {
        val userId = auth.currentUser?.uid
        val dataOrException = DataOrException<Boolean, Boolean, Exception>()

        try {
            dataOrException.isLoading = true
            if (userId != null) {
                firebaseFirestore.collection("users").document(userId).update(user)
                    .addOnCompleteListener {
                        Log.d("UserRepository", "updateUser:success")
                        dataOrException.data = true
                        dataOrException.isLoading = false
                    }
                    .addOnFailureListener {
                        Log.d("UserRepository", "updateUser:failure")
                        dataOrException.data = false
                        dataOrException.isLoading = false
                    }.await()
            }
        } catch (e: Exception) {
            dataOrException.exception = e
            dataOrException.isLoading = false
        }
        return dataOrException
    }

    suspend fun updateUserEmail(email: String){
        try {
            auth.currentUser?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("UserRepository", "updateUserEmail:success")
                } else {
                    Log.d("UserRepository", "updateUserEmail:failure")
                }
            }?.await()
        } catch (e: Exception) {
            Log.d("UserRepository", "updateUserEmail:failure")
        }
    }

    suspend fun updateUserPassword(password: String){

        try {
            auth.currentUser?.updatePassword(password)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("UserRepository", "updateUserPassword:success")
                } else {
                    Log.d("UserRepository", "updateUserPassword:failure")
                }
            }?.await()
        } catch (e: Exception) {
            Log.d("UserRepository", "updateUserPassword:failure")
        }
    }

    suspend fun uploadImageToFirebaseStorage(uri: Uri): DataOrException<String, Boolean, Exception> {
        val avatarUri = DataOrException<String, Boolean, Exception>()
        val storageRef = FirebaseStorage.getInstance().reference
        val userId = auth.currentUser?.uid
        val imageRef = storageRef.child("profile_images/$userId.jpg")

        return try {
            avatarUri.isLoading = true
            imageRef.putFile(uri).await()
            val downloadUri = imageRef.downloadUrl.await()
            avatarUri.data = downloadUri.toString()
            Log.d("UserRepository", "URL: $downloadUri")
            avatarUri.isLoading = false
            avatarUri
        } catch (e: Exception) {
            avatarUri.exception = e
            avatarUri.isLoading = false
            avatarUri
        }
    }
}