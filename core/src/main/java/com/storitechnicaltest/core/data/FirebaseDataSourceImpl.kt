package com.storitechnicaltest.core.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.storitechnicaltest.core.model.Transaction
import com.storitechnicaltest.core.model.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseDataSourceImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage
) : FirebaseDataSource {

    override suspend fun registerUser(user: User): String? {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(user.email, user.password)
            .await()
        return result.user?.uid
    }

    override suspend fun registerUserInFirestore(user: User) {
        fireStore.collection("users")
            .document(user.id)
            .set(user)
            .await()
    }

    override suspend fun uploadPhoto(photoUri: Uri): String {
        val ref = storage.reference.child("photos/${UUID.randomUUID()}.jpg")
        ref.putFile(photoUri).await()
        return ref.downloadUrl.await().toString()
    }

    override suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserData(): DocumentSnapshot? {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: ""
            fireStore.collection("users")
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            null
        }
    }

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser?.uid != null

    override fun logout() = firebaseAuth.signOut()

    override suspend fun pushTransactions(transactions: List<Transaction>) {
        val userId =
            firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        val batch = fireStore.batch()

        transactions.forEach { transaction ->
            val docRef = fireStore
                .collection("users")
                .document(userId)
                .collection("transactions")
                .document(transaction.id)

            batch.set(docRef, transaction)
        }

        batch.commit().await()
    }

    override suspend fun getTransactions(): List<DocumentSnapshot> {
        val userId =
            firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

        val snapshot = fireStore
            .collection("users")
            .document(userId)
            .collection("transactions")
            .get()
            .await()

        return snapshot.documents
    }

    override suspend fun getTransactionById(transactionId: String): DocumentSnapshot? {
        val userId =
            firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
        return fireStore.collection("users")
            .document(userId)
            .collection("transactions")
            .document(transactionId)
            .get()
            .await()
    }
}
