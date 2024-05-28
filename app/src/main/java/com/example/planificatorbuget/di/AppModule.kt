package com.example.planificatorbuget.di

import com.example.planificatorbuget.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

}