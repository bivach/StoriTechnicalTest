package com.storitechnicaltest.core.data

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.storitechnicaltest.core.model.Transaction
import com.storitechnicaltest.core.model.User

interface FirebaseDataSource {
    /**
     * Registers a new user with Firebase Authentication using the provided user details.
     *
     * @param user The user details including email and password.
     * @return The unique user ID (UID) of the registered user if registration is successful, null otherwise.
     * @throws FirebaseAuthException if the registration fails.
     */
    suspend fun registerUser(user: User): String?
    /**
     * Registers the user in Firestore by saving the user details in the "users" collection.
     *
     * @param user The user details including user ID to be saved in Firestore.
     * @throws FirestoreException if the Firestore operation fails.
     */
    suspend fun registerUserInFirestore(user: User)

    /**
     * Uploads a photo to Firebase Storage and returns the download URL of the uploaded photo.
     *
     * @param photoUri The URI of the photo to be uploaded.
     * @return The download URL of the uploaded photo.
     * @throws StorageException if the upload operation fails.
     */
    suspend fun uploadPhoto(photoUri: Uri): String?

    /**
     * Attempts to log in a user with the provided email and password using Firebase authentication.
     *
     * This method attempts to authenticate the user by calling signInWithEmailAndPassword()
     * with the provided email and password. If the authentication is successful, the method returns true.
     * Otherwise, if any exception occurs during the authentication process, the method returns false.
     *
     * @param email The email of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return true if the user is successfully logged in, false otherwise.
     */
    suspend fun loginUser(email: String, password: String): Boolean

    /**
     * Retrieves the data of the currently authenticated user from Firestore.
     *
     * This method retrieves the data of the currently authenticated user from Firestore
     * by querying the "users" collection with the user's unique ID. If the user is not
     * logged in or an exception occurs during the process, null is returned.
     *
     * @return The DocumentSnapshot containing the user's data, or null if not found or an error occurs.
     */
    suspend fun getUserData(): DocumentSnapshot?

    /**
     * Checks if a user is currently logged in.
     *
     * This method checks if a user is currently logged in by verifying the presence
     * of a user ID in the FirebaseAuth instance.
     *
     * @return true if a user is logged in, false otherwise.
     */
    fun isUserLoggedIn(): Boolean

    /**
     * Logs out the currently authenticated user.
     *
     * This method signs out the currently authenticated user using the FirebaseAuth instance.
     */
    fun logout()

    /**
     * Pushes a list of transactions to Firestore under the current user's account.
     *
     * This method creates a batch operation to push the provided list of transactions
     * to Firestore under the current user's account. If the user is not logged in,
     * an IllegalStateException is thrown.
     *
     * @param transactions The list of transactions to push to Firestore.
     */
    suspend fun pushTransactions(transactions: List<Transaction>)

    /**
     * Retrieves the list of transactions from Firestore for the current user.
     *
     * This method retrieves the list of transactions from Firestore for the current
     * user by querying the "transactions" subcollection under the user's account.
     * If the user is not logged in or an exception occurs during the process, an
     * empty list is returned.
     *
     * @return The list of DocumentSnapshots representing the user's transactions.
     */
    suspend fun getTransactions(): List<DocumentSnapshot>

    /**
     * Retrieves a specific transaction from Firestore based on its ID.
     *
     * @param transactionId The ID of the transaction to retrieve.
     * @return A [DocumentSnapshot] representing the transaction if found, or null if an error occurs.
     */
    suspend fun getTransactionById(transactionId: String): DocumentSnapshot?
}
