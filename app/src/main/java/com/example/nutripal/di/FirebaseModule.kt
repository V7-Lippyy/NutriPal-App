package com.example.nutripal.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.nutripal.data.repository.AuthRepository
import com.example.nutripal.data.repository.FirebaseFoodEntryRepository
import com.example.nutripal.data.repository.FirebaseUserRepository
import com.example.nutripal.domain.repository.IAuthRepository
import com.example.nutripal.domain.repository.IFoodEntryRepository
import com.example.nutripal.domain.repository.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideAuthRepository(
        firestore: FirebaseFirestore
    ): IAuthRepository = AuthRepository(firestore)

    @Provides
    @Singleton
    @Named("FirebaseRepository")
    fun provideFoodEntryRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): IFoodEntryRepository = FirebaseFoodEntryRepository(firestore, auth)

    @Provides
    @Singleton
    @Named("FirebaseUserRepository")
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): IUserRepository = FirebaseUserRepository(firestore, auth)
}