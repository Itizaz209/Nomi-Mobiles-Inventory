package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyUtils {
    private val pkrFormatter = NumberFormat.getNumberInstance(Locale.US).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
    private val shortDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)

    fun formatPkr(amount: Double): String {
        return "Rs ${pkrFormatter.format(amount)}"
    }

    fun formatDate(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        return shortDateFormatter.format(Date(timestamp))
    }
}
