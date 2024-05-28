package com.storitechnicaltest.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals
import com.google.firebase.firestore.DocumentSnapshot
import com.storitechnicaltest.core.data.FirebaseDataSource
import com.storitechnicaltest.core.domain.RandomTransactionsGenerator
import com.storitechnicaltest.core.domain.TransactionsRepositoryImpl
import com.storitechnicaltest.core.model.Transaction
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TransactionsRepositoryImplTest {

    @Mock
    private lateinit var mockDataSource: FirebaseDataSource

    @Mock
    private lateinit var mockRandomTransactionsGenerator: RandomTransactionsGenerator


    private lateinit var transactionsRepository: TransactionsRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        transactionsRepository = TransactionsRepositoryImpl(mockDataSource, mockRandomTransactionsGenerator)
    }

    @Test
    fun `test getUserTransactions with transactions`() = runTest {
        val mockTransaction = Transaction("id", 100.0, "2021-12-01")
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDocumentSnapshot.toObject(Transaction::class.java)).thenReturn(mockTransaction)
        `when`(mockDataSource.getTransactions()).thenReturn(listOf(mockDocumentSnapshot))

        val result = transactionsRepository.getUserTransactions()

        verify(mockDataSource, times(1)).getTransactions()
        assertEquals(listOf(mockTransaction), result)
    }

    @Test
    fun `test getUserTransactions without transactions`() = runTest {
        // Mock the data source to return an empty list
        `when`(mockDataSource.getTransactions()).thenReturn(emptyList())

        // Mock the random transactions to return a specific set
        val randomTransactions = listOf(
            Transaction("Transaction 1", 500.0, "2021-12-01"),
            Transaction("Transaction 2", 200.0, "2021-12-02")
        )

        // Mock the random transactions generator to return the above list
        `when`(mockRandomTransactionsGenerator.generateRandomTransactions())
            .thenReturn(randomTransactions)

        // Call the method under test
        val result = transactionsRepository.getUserTransactions()

        // Verify the interactions and assert the results
        verify(mockDataSource).getTransactions()
        verify(mockDataSource).pushTransactions(randomTransactions)
        assertEquals(randomTransactions, result)
    }

    @Test
    fun `test getUserTransactions with exception`() = runTest {
        `when`(mockDataSource.getTransactions()).thenThrow(RuntimeException())

        val result = transactionsRepository.getUserTransactions()

        verify(mockDataSource).getTransactions()
        assertEquals(emptyList<Transaction>(), result)
    }

    @Test
    fun `test getTransactionById with valid transactionId`() = runTest {
        // Given
        val transactionId = "123456"
        val expectedTransaction = Transaction(/* Your expected transaction object */)
        val documentSnapshot: DocumentSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockDataSource.getTransactionById(transactionId)).thenReturn(documentSnapshot)
        `when`(documentSnapshot.toObject(Transaction::class.java)).thenReturn(expectedTransaction)

        // When
        val result = transactionsRepository.getTransactionById(transactionId)

        // Then
        assertEquals(expectedTransaction, result)
    }

    @Test
    fun `test getTransactionById with invalid transactionId`() = runTest {
        // Given
        val transactionId = "invalid_id"
        `when`(mockDataSource.getTransactionById(transactionId)).thenReturn(null)

        // When
        val result = transactionsRepository.getTransactionById(transactionId)

        // Then
        assertEquals(null, result)
    }
}
