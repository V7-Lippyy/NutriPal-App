package com.example.nutripal.data.repository

import com.example.nutripal.domain.model.UserData
import com.example.nutripal.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : IUserRepository {

    private val usersCollection = "users"
    private val preferencesDocument = "preferences"

    override fun getUserData(): Flow<UserData> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(UserData())
            return@callbackFlow
        }

        // Refresh token to keep session alive
        try {
            currentUser.getIdToken(true).await()
        } catch (_: Exception) {
            trySend(UserData())
            return@callbackFlow
        }

        val userId = currentUser.uid
        val preferencesRef = firestore.collection(usersCollection)
            .document(userId)
            .collection("user_data")
            .document(preferencesDocument)

        val listenerRegistration = preferencesRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null || !snapshot.exists()) {
                trySend(UserData())
                return@addSnapshotListener
            }

            val userData = UserData(
                name = snapshot.getString("name") ?: "",
                hasCompletedOnboarding = snapshot.getBoolean("hasCompletedOnboarding") ?: false
            )
            trySend(userData)
        }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun saveUserData(name: String) {
        val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

        try {
            currentUser.getIdToken(true).await()
        } catch (_: Exception) {
            throw Exception("Token expired. Please re-login.")
        }

        val userId = currentUser.uid
        val preferencesRef = firestore.collection(usersCollection)
            .document(userId)
            .collection("user_data")
            .document(preferencesDocument)

        val userData = mapOf(
            "name" to name,
            "hasCompletedOnboarding" to true
        )

        withTimeout(10000) {
            preferencesRef.set(userData).await()
        }
    }

    override suspend fun completeOnboarding() {
        val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

        try {
            currentUser.getIdToken(true).await()
        } catch (_: Exception) {
            throw Exception("Token expired. Please re-login.")
        }

        val userId = currentUser.uid
        val preferencesRef = firestore.collection(usersCollection)
            .document(userId)
            .collection("user_data")
            .document(preferencesDocument)

        withTimeout(10000) {
            preferencesRef.update("hasCompletedOnboarding", true).await()
        }
    }

    override suspend fun hasCompletedOnboarding(): Boolean {
        val currentUser = auth.currentUser ?: return false

        try {
            currentUser.getIdToken(true).await()
        } catch (_: Exception) {
            return false
        }

        val userId = currentUser.uid
        val preferencesRef = firestore.collection(usersCollection)
            .document(userId)
            .collection("user_data")
            .document(preferencesDocument)

        return try {
            val snapshot = withTimeout(10000) { preferencesRef.get().await() }
            snapshot.exists() && snapshot.getBoolean("hasCompletedOnboarding") == true
        } catch (_: Exception) {
            false
        }
    }
}
