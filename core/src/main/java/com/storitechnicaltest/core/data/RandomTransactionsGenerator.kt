package com.storitechnicaltest.core.data

import com.storitechnicaltest.core.domain.model.Transaction
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

open class RandomTransactionsGenerator @Inject constructor() {

    open fun generateRandomTransactions(): List<Transaction> {
        val randomAmountOfTransactions = Random.nextInt(1, 10)
        val transactions = mutableListOf<Transaction>()
        repeat(randomAmountOfTransactions) { index ->
            val transactionId = "Transaction ${index + 1}"
            val transaction = Transaction(
                id = transactionId,
                amount = Random.nextDouble(1.0, 1000.0),
                date = randomDateBetween().toString()
            )
            transactions.add(transaction)
        }
        return transactions
    }

    private fun randomDateBetween(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(2020, Calendar.JANUARY, 1)
        val startDate = calendar.time
        val startMillis = startDate.time
        val endMillis = Date().time
        val randomMillis = Random.nextLong(startMillis, endMillis)
        return Date(randomMillis)
    }
}
