package com.example.planificatorbuget.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryIconsRepository @Inject constructor(private val firebaseStorage: FirebaseStorage, private val auth: FirebaseAuth) {
    suspend fun fetchIconsFromFirestore(): List<String> {
        return try {
            val icons = firebaseStorage.reference.child("category_icons").listAll().await()
            val iconsUrls = mutableListOf<String>()
            icons.items.forEach {
                val url = it.downloadUrl.await()
                iconsUrls.add(url.toString())
            }
            iconsUrls
        } catch (e: Exception) {
            emptyList()
        }
    }
}