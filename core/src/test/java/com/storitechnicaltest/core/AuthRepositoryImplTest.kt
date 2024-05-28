package com.storitechnicaltest.core

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.storitechnicaltest.core.data.FirebaseDataSource
import com.storitechnicaltest.core.domain.AuthRepositoryImpl
import com.storitechnicaltest.core.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryImplTest {

    @Mock
    private lateinit var dataSource: FirebaseDataSource

    @Mock
    private lateinit var photoUri: Uri


    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun testRegisterUserSuccess() = runTest {
        // Given
        val user = User("test@example.com", "password")

        // Mock the dataSource to return true (registration success)
        `when`(dataSource.registerUser(user, photoUri)).thenReturn(true)

        // When
        val result = authRepository.registerUser(user, photoUri)

        // Then
        assertEquals(true, result)
    }

    @Test
    fun testRegisterUserFailure() = runTest {
        // Given
        val user = User("test@example.com", "password")

        // Mock the dataSource to return false (registration failure)
        `when`(dataSource.registerUser(user, photoUri)).thenReturn(false)

        // When
        val result = authRepository.registerUser(user, photoUri)

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `test loginUser`() = runTest {
        val email = "test@example.com"
        val password = "password"
        `when`(dataSource.loginUser(email, password)).thenReturn(true)

        val result = authRepository.loginUser(email, password)

        verify(dataSource).loginUser(email, password)
        assertEquals(true, result)
    }

    @Test
    fun `test getUserData`() = runTest {
        val mockUser = User("test@example.com", "password", "John", "Doe")
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(mockUser)
        `when`(dataSource.getUserData()).thenReturn(mockDocumentSnapshot)

        val result = authRepository.getUserData()

        verify(dataSource).getUserData()
        assertEquals(mockUser, result)
    }

    @Test
    fun `test isUserLoggedIn`() {
        `when`(dataSource.isUserLoggedIn()).thenReturn(true)

        val result = authRepository.isUserLoggedIn()

        verify(dataSource).isUserLoggedIn()
        assertEquals(true, result)
    }

    @Test
    fun `test logout`() {
        doNothing().`when`(dataSource).logout()

        authRepository.logout()

        verify(dataSource).logout()
    }
}
