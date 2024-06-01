package com.storitechnicaltest.core.domain.model

import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val photoUrl: String = "",
    val balance:Double = Random.nextDouble(1.0, 100000.0)
) {
    /**
     * Formats the transaction amount into a currency string.
     *
     * @param locale The locale to be used for formatting the currency. Default is Locale.US.
     * @return A formatted currency string.
     */
    fun formatBalance(locale: Locale = Locale.US): String {
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        return numberFormat.format(balance)
    }
}
