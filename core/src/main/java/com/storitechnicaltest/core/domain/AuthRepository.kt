package com.storitechnicaltest.core.domain

import android.net.Uri
import com.storitechnicaltest.core.domain.model.User

/**
 * Repository interface for user authentication-related operations.
 */
interface AuthRepository {

    /**
     * Registers a new user by uploading a photo, creating a Firebase Authentication user,
     * and saving the user details in Firestore.
     *
     * @param user The user details including email and password.
     * @param photoUri The URI of the photo to be uploaded.
     * @return `true` if the registration is successful, `false` otherwise.
     * @throws Exception if any operation in the registration process fails.
     */
    suspend fun registerUser(user: User, photoUri: Uri): Boolean

    /**
     * Logs in an existing user.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return True if the user is successfully logged in, false otherwise.
     */
    suspend fun loginUser(email: String, password: String): Boolean

    /**
     * Retrieves the data of the currently logged-in user and maps it to domain User.
     *
     * @return The user object if logged in, null otherwise.
     */
    suspend fun getUserData(): User?

    /**
     * Determinate if user has already login
     *
     *
     * @return The boolean state.
     */
    fun isUserLoggedIn(): Boolean

    /**
     * Logout current user
     */
    fun logout()
}
