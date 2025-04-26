package com.example.nutripal.data.repository

import android.util.Log
import com.example.nutripal.domain.model.AuthResult
import com.example.nutripal.domain.model.User
import com.example.nutripal.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay
import kotlinx.coroutines.TimeoutCancellationException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "AuthRepository"

@Singleton
class AuthRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : IAuthRepository {

    private val auth: FirebaseAuth = Firebase.auth

    init {
        firestore.enableNetwork()
    }

    override suspend fun register(email: String, username: String, password: String): AuthResult<User> {
        return try {
            firestore.enableNetwork()
            delay(500)

            val usernameQuery = withTimeout(15000) {
                firestore.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .await()
            }

            if (!usernameQuery.isEmpty) {
                return AuthResult.Error("Username sudah digunakan")
            }

            val authResult = withTimeout(15000) {
                auth.createUserWithEmailAndPassword(email, password).await()
            }

            val userId = authResult.user?.uid ?: return AuthResult.Error("Gagal mendapatkan UID")

            try {
                authResult.user?.sendEmailVerification()?.await()
            } catch (_: Exception) {}

            val user = User(
                userId = userId,
                email = email,
                username = username,
                createdAt = System.currentTimeMillis(),
                lastLoginAt = System.currentTimeMillis()
            )

            withTimeout(15000) {
                firestore.collection("users").document(userId).set(user).await()
            }

            AuthResult.Success(user)
        } catch (e: TimeoutCancellationException) {
            AuthResult.Error("Waktu koneksi habis. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Terjadi kesalahan saat registrasi")
        }
    }

    override suspend fun login(emailOrUsername: String, password: String): AuthResult<User> {
        return try {
            firestore.enableNetwork()
            delay(500)

            val email = if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
                emailOrUsername
            } else {
                withTimeout(15000) {
                    val userQuery = firestore.collection("users")
                        .whereEqualTo("username", emailOrUsername)
                        .limit(1)
                        .get()
                        .await()
                    if (userQuery.isEmpty) return@withTimeout null
                    userQuery.documents.first().getString("email")
                } ?: return AuthResult.Error("Username tidak ditemukan atau koneksi terputus")
            }

            val authResult = withTimeout(15000) {
                auth.signInWithEmailAndPassword(email, password).await()
            }

            val userId = authResult.user?.uid ?: return AuthResult.Error("Gagal mendapatkan UID")

            // Refresh token explicitly
            try {
                authResult.user?.getIdToken(true)?.await()
            } catch (e: Exception) {
                Log.w(TAG, "Token refresh failed: ${e.message}")
            }

            val userDoc = withTimeout(15000) {
                firestore.collection("users").document(userId).get().await()
            }

            val user = userDoc.toObject(User::class.java)
                ?: return AuthResult.Error("Gagal mendapatkan data pengguna")

            try {
                firestore.collection("users").document(userId)
                    .update("lastLoginAt", System.currentTimeMillis()).await()
            } catch (_: Exception) {}

            AuthResult.Success(user)
        } catch (e: TimeoutCancellationException) {
            AuthResult.Error("Waktu koneksi habis. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Terjadi kesalahan saat login")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        return try {
            firestore.enableNetwork()
            delay(500)
            withTimeout(15000) {
                auth.sendPasswordResetEmail(email).await()
            }
            AuthResult.Success(Unit)
        } catch (e: TimeoutCancellationException) {
            AuthResult.Error("Waktu koneksi habis. Periksa koneksi internet Anda")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Terjadi kesalahan saat mengirim email reset password")
        }
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firebaseUser.getIdToken(true).addOnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        Log.w(TAG, "Token refresh failed: ${tokenTask.exception?.message}")
                        trySend(null)
                        return@addOnCompleteListener
                    }

                    val userId = firebaseUser.uid
                    firestore.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener { document ->
                            val user = document.toObject(User::class.java)
                            trySend(user)
                        }
                        .addOnFailureListener {
                            trySend(null)
                        }
                }
            }
        }

        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            auth.signOut()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Terjadi kesalahan saat logout")
        }
    }

    override fun isUserAuthenticated(): Flow<Boolean> = flow {
        emit(auth.currentUser != null)
    }
}
