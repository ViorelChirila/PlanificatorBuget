package com.example.planificatorbuget.di

import com.example.planificatorbuget.repository.CategoryIconsRepository
import com.example.planificatorbuget.repository.TransactionRepository
import com.example.planificatorbuget.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository =
        UserRepository(firebaseFirestore = FirebaseFirestore.getInstance(), auth = FirebaseAuth.getInstance())

    @Singleton
    @Provides
    fun provideTransactionRepository(): TransactionRepository =
        TransactionRepository(firebaseFirestore = FirebaseFirestore.getInstance(), auth = FirebaseAuth.getInstance())

    @Singleton
    @Provides
    fun provideCategoryIconsRepository(): CategoryIconsRepository =
        CategoryIconsRepository(firebaseStorage = Firebase.storage, auth = FirebaseAuth.getInstance())
}