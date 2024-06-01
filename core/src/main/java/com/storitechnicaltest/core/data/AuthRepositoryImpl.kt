package com.storitechnicaltest.core.data

import android.net.Uri
import com.storitechnicaltest.core.domain.AuthRepository
import com.storitechnicaltest.core.domain.model.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseDataSource
) : AuthRepository {

    override suspend fun registerUser(user: User, photoUri: Uri): Boolean {
        return try {
            // Upload user photo file
            val photoUrl = dataSource.uploadPhoto(photoUri) ?: return false
            // Create Firebase user with firebaseAuth
            val registeredUserId = dataSource.registerUser(user) ?: return false
            // Save user to fireStore
            dataSource.registerUserInFirestore(
                user.copy(
                    photoUrl = photoUrl,
                    id = registeredUserId
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun loginUser(email: String, password: String) =
        dataSource.loginUser(email, password)

    override suspend fun getUserData() = dataSource.getUserData()?.toObject(User::class.java)

    override fun isUserLoggedIn(): Boolean = dataSource.isUserLoggedIn()

    override fun logout() = dataSource.logout()

}

