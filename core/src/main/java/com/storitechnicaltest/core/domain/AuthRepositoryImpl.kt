package com.storitechnicaltest.core.domain

import android.net.Uri
import com.storitechnicaltest.core.data.FirebaseDataSource
import com.storitechnicaltest.core.model.User
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseDataSource
) : AuthRepository {

    override suspend fun registerUser(user: User, photoUri: Uri): Boolean =
        dataSource.registerUser(user, photoUri)

    override suspend fun loginUser(email: String, password: String) =
        dataSource.loginUser(email, password)

    override suspend fun getUserData() = dataSource.getUserData()?.toObject(User::class.java)

    override fun isUserLoggedIn(): Boolean = dataSource.isUserLoggedIn()

    override fun logout() = dataSource.logout()

}
