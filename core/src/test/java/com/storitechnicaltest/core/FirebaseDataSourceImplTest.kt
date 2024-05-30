package com.storitechnicaltest.core

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.storage.FirebaseStorage
import com.storitechnicaltest.core.data.FirebaseDataSourceImpl
import com.storitechnicaltest.core.model.Transaction
import com.storitechnicaltest.core.model.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FirebaseDataSourceImplTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockFirebaseStorage: FirebaseStorage

    @Mock
    private lateinit var mockCollectionReference: CollectionReference

    @Mock
    private lateinit var mockDocumentReference: DocumentReference

    @Mock
    private lateinit var mockDocumentSnapshot: DocumentSnapshot

    private lateinit var firebaseDataSource: FirebaseDataSourceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        firebaseDataSource = FirebaseDataSourceImpl(mockFirestore, mockFirebaseAuth, mockFirebaseStorage)
    }

    @Test
    fun `registerUser calls createUserWithEmailAndPassword`() = runTest {
        // Given
        val user = User(id = "123", email = "test@example.com", password = "password")
        val mockAuthResult: AuthResult = mock(AuthResult::class.java)
        val mockUser: FirebaseUser = mock(FirebaseUser::class.java)
        `when`(mockAuthResult.user).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("123")
        `when`(mockFirebaseAuth.createUserWithEmailAndPassword(user.email, user.password)).thenReturn(Tasks.forResult(mockAuthResult))

        // When
        val result = firebaseDataSource.registerUser(user)

        // Then
        verify(mockFirebaseAuth).createUserWithEmailAndPassword(user.email, user.password)
        assertEquals("123", result)
    }

    @Test
    fun `registerUserInFirestore calls set on firestore`() = runTest {
        // Given
        val user = User(id = "123", email = "test@example.com", password = "password")
        val mockDocumentReference: DocumentReference = mock(DocumentReference::class.java)
        `when`(mockFirestore.collection("users")).thenReturn(mock(CollectionReference::class.java))
        `when`(mockFirestore.collection("users").document(user.id)).thenReturn(mockDocumentReference)
        `when`(mockDocumentReference.set(user)).thenReturn(Tasks.forResult(null))

        // When
        firebaseDataSource.registerUserInFirestore(user)

        // Then
        verify(mockFirestore.collection("users").document(user.id)).set(user)
    }

    @Test
    fun `loginUser calls signInWithEmailAndPassword and returns true on success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(Tasks.forResult(mock(AuthResult::class.java)))

        // When
        val result = firebaseDataSource.loginUser(email, password)

        // Then
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
        assertTrue(result)
    }

    @Test
    fun `loginUser calls signInWithEmailAndPassword and returns false on failure`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password"
        `when`(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(Tasks.forException(Exception()))

        // When
        val result = firebaseDataSource.loginUser(email, password)

        // Then
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
        assertFalse(result)
    }

    @Test
    fun `getUserData retrieves user data from firestore`() = runTest {
        // Given
        val userId = "123"
        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(userId)).thenReturn(mockDocumentReference)

        // Mocking the Firestore get operation
        val task: Task<DocumentSnapshot> = Tasks.forResult(mockDocumentSnapshot)
        `when`(mockDocumentReference.get()).thenReturn(task)

        // When
        val result = firebaseDataSource.getUserData()

        // Then
        verify(mockCollectionReference).document(userId)
        verify(mockDocumentReference).get()
        assert(result == mockDocumentSnapshot)
    }

    @Test
    fun `isUserLoggedIn calls currentUser on firebaseAuth and returns true when user is not null`() {
        val userId = "123"
        // When
        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)

        val result = firebaseDataSource.isUserLoggedIn()

        // Then
        verify(mockFirebaseAuth, times(2)).currentUser
        verify(mockFirebaseAuth, times(2)).currentUser?.uid
        assertTrue(result)
    }

    @Test
    fun `isUserLoggedIn calls currentUser on firebaseAuth and returns false when null`() {
        // Given
        val userId = null
        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)

        //When
        val result = firebaseDataSource.isUserLoggedIn()

        // Then
        verify(mockFirebaseAuth, times(2)).currentUser
        verify(mockFirebaseAuth, times(2)).currentUser?.uid
        assertFalse(result)
    }

    @Test
    fun `logout calls signOut on firebaseAuth`() {
        // When
        firebaseDataSource.logout()

        // Then
        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun `pushTransactions calls batch set and commit`() = runTest {
        // Given
        val userId = "123"
        val transactions = listOf(
            Transaction(id = "1", amount = 100.0, date = "2023-01-01"),
            Transaction(id = "2", amount = 200.0, date = "2023-01-02")
        )
        val mockBatch: WriteBatch = mock(WriteBatch::class.java)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockFirestore.batch()).thenReturn(mockBatch)
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId)).thenReturn(mockDocumentReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions").document("1")).thenReturn(mockDocumentReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions").document("2")).thenReturn(mockDocumentReference)
        `when`(mockBatch.set(any(DocumentReference::class.java), any(Transaction::class.java))).thenReturn(mockBatch)
        `when`(mockBatch.commit()).thenReturn(Tasks.forResult(null))

        // When
        firebaseDataSource.pushTransactions(transactions)

        // Then
        verify(mockBatch, times(transactions.size)).set(any(DocumentReference::class.java), any(Transaction::class.java))
        verify(mockBatch).commit()
    }


    @Test
    fun `getTransactions returns list of DocumentSnapshot`() = runTest {
        // Given
        val userId = "123"
        val mockQuerySnapshot: QuerySnapshot = mock(QuerySnapshot::class.java)
        val mockDocumentSnapshots = listOf(mock(DocumentSnapshot::class.java))

        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId)).thenReturn(mockDocumentReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions").get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        `when`(mockQuerySnapshot.documents).thenReturn(mockDocumentSnapshots)

        // When
        val result = firebaseDataSource.getTransactions()

        // Then
        verify(mockFirestore.collection("users").document(userId).collection("transactions")).get()
        assertEquals(mockDocumentSnapshots, result)
    }

    @Test
    fun `getTransactionById returns DocumentSnapshot`() = runTest {
        // Given
        val userId = "123"
        val transactionId = "456"
        val mockDocumentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)

        `when`(mockFirebaseAuth.currentUser).thenReturn(mock(FirebaseUser::class.java))
        `when`(mockFirebaseAuth.currentUser?.uid).thenReturn(userId)
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId)).thenReturn(mockDocumentReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions")).thenReturn(mockCollectionReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions").document(transactionId)).thenReturn(mockDocumentReference)
        `when`(mockFirestore.collection("users").document(userId).collection("transactions").document(transactionId).get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

        // When
        val result = firebaseDataSource.getTransactionById(transactionId)

        // Then
        verify(mockFirestore.collection("users").document(userId).collection("transactions").document(transactionId)).get()
        assertEquals(mockDocumentSnapshot, result)
    }
}
