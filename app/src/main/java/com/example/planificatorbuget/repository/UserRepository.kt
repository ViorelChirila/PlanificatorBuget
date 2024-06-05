package com.example.planificatorbuget.repository

import android.net.Uri
import android.util.Log
import com.example.planificatorbuget.data.DataOrException
import com.example.planificatorbuget.data.Response
import com.example.planificatorbuget.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "UserRepository"
        private const val USERS_COLLECTION = "users"
        private const val PROFILE_IMAGES_PATH = "profile_images"
    }

    suspend fun fetchUser(): DataOrException<UserModel, Boolean, Exception> {
        val dataOrExceptionUserData = DataOrException<UserModel, Boolean, Exception>()
        val userId = auth.currentUser?.uid ?: return dataOrExceptionUserData.apply {
            isLoading = false
        }

        dataOrExceptionUserData.isLoading = true
        return try {
            val documentSnapshot =
                firebaseFirestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = documentSnapshot.toObject(UserModel::class.java)
            dataOrExceptionUserData.data = user
            dataOrExceptionUserData.isLoading = false
            dataOrExceptionUserData
        } catch (e: Exception) {
            dataOrExceptionUserData.exception = e
            dataOrExceptionUserData.isLoading = false
            dataOrExceptionUserData
        }
    }

suspend fun updateUserData(user: Map<String, Any>): Response<Boolean> {
    val userId = auth.currentUser?.uid ?: return Response.Error("User not authenticated")

    return try {
        firebaseFirestore.collection(USERS_COLLECTION).document(userId).update(user).await()
        Response.Success(true)
    } catch (e: Exception) {
        Response.Error(e.message, false)
    }
}


    suspend fun updateUserEmail(email: String) {
        try {
            auth.currentUser?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "updateUserEmail:success")
                } else {
                    Log.d(TAG, "updateUserEmail:failure")
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
                    Log.d(TAG, "updateUserPassword:success")
                } else {
                    Log.d(TAG, "updateUserPassword:failure")
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
        val imageRef = storageRef.child("$PROFILE_IMAGES_PATH/$userId.jpg")

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

    suspend fun updateUserCurrentBudget(budget: Double): Response<Boolean> {
        val userId = auth.currentUser?.uid ?: return Response.Error("User not authenticated")
        return try {
            firebaseFirestore.collection(USERS_COLLECTION).document(userId).update("current_budget", budget).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Error(e.message, false)
        }
    }
}