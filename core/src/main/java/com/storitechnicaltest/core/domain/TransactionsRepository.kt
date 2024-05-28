package com.storitechnicaltest.core.domain

import com.storitechnicaltest.core.model.Transaction


/**
 * Repository interface for managing transactions.
 */
interface TransactionsRepository {
    /**
     * Retrieves user transactions.
     * This function retrieves the list of transactions associated with the current user.
     * @return A list of transactions.
     */
    suspend fun getUserTransactions(): List<Transaction>

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param transactionId The ID of the transaction to retrieve.
     * @return The [Transaction] object representing the transaction if found, or null if not found or an error occurs.
     */
    suspend fun getTransactionById(transactionId: String): Transaction?
}
