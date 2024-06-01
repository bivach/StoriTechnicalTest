package com.storitechnicaltest.core.domain.model

import java.text.NumberFormat
import java.util.Locale

data class Transaction(
    val id: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = ""
) {
    /**
     * Formats the transaction amount into a currency string.
     *
     * @param locale The locale to be used for formatting the currency. Default is Locale.US.
     * @return A formatted currency string.
     */
    fun formatAmount(locale: Locale = Locale.US): String {
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        return numberFormat.format(amount)
    }
}
