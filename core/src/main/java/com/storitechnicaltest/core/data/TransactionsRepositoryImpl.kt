package com.storitechnicaltest.core.data

import com.storitechnicaltest.core.domain.TransactionsRepository
import com.storitechnicaltest.core.domain.model.Transaction
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseDataSource,
    private val randomTransactionsGenerator: RandomTransactionsGenerator
) : TransactionsRepository {

    override suspend fun getUserTransactions(): List<Transaction> {
        return try {
            val getTransactions = dataSource.getTransactions()
            return if (getTransactions.isNotEmpty()) {
                getTransactions.mapNotNull { document ->
                    document.toObject(Transaction::class.java)
                }
            } else {
                randomTransactionsGenerator.generateRandomTransactions().also {
                    dataSource.pushTransactions(it)
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTransactionById(transactionId: String): Transaction? {
        return try {
            val document = dataSource.getTransactionById(transactionId)
            document?.toObject(Transaction::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
