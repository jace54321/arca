package com.arca.android.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Utility extension functions used across the app.
 */

/**
 * Format a timestamp string to a human-readable format.
 * Handles ISO-8601 and common timestamp formats.
 */
fun String.toDisplayTimestamp(): String {
    return try {
        val dateTime = LocalDateTime.parse(this.substringBefore("+").substringBefore("Z"))
        dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' hh:mm a"))
    } catch (_: DateTimeParseException) {
        this // Return as-is if parsing fails
    }
}

/**
 * Mask an email address for display (show first 3 chars + domain).
 * "user@example.com" → "use•••@example.com"
 */
fun String.maskEmail(): String {
    val parts = this.split("@")
    if (parts.size != 2) return this

    val local = parts[0]
    val domain = parts[1]
    val visible = local.take(3)
    val masked = "•".repeat((local.length - 3).coerceAtLeast(0))

    return "$visible$masked@$domain"
}

/**
 * Mask a username for display.
 * "john.doe@gmail.com" → "joh•••••••••••••••"
 */
fun String.maskUsername(): String {
    if (this.length <= 3) return this
    return this.take(3) + "•".repeat((this.length - 3).coerceAtMost(12))
}
