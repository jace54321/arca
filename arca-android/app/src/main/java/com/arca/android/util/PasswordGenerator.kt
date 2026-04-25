package com.arca.android.util

import java.security.SecureRandom

/**
 * Generates strong random passwords for new credential entries.
 */
object PasswordGenerator {

    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#\$%^&*()_+-=[]{}|;:',.<>?"

    /**
     * Generate a cryptographically secure random password.
     *
     * @param length Password length (default 20, min 8, max 128)
     * @param includeUppercase Include uppercase letters
     * @param includeDigits Include digits
     * @param includeSymbols Include symbol characters
     */
    fun generate(
        length: Int = 20,
        includeUppercase: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
    ): String {
        val safeLength = length.coerceIn(8, 128)
        val random = SecureRandom()

        // Build character pool
        val pool = StringBuilder(LOWERCASE)
        if (includeUppercase) pool.append(UPPERCASE)
        if (includeDigits) pool.append(DIGITS)
        if (includeSymbols) pool.append(SYMBOLS)

        val chars = pool.toString()

        // Generate password ensuring at least one char from each required category
        val required = mutableListOf<Char>()
        required.add(LOWERCASE[random.nextInt(LOWERCASE.length)])
        if (includeUppercase) required.add(UPPERCASE[random.nextInt(UPPERCASE.length)])
        if (includeDigits) required.add(DIGITS[random.nextInt(DIGITS.length)])
        if (includeSymbols) required.add(SYMBOLS[random.nextInt(SYMBOLS.length)])

        // Fill the rest randomly
        val remaining = safeLength - required.size
        val allChars = required + (0 until remaining).map { chars[random.nextInt(chars.length)] }

        // Shuffle to avoid predictable positions
        return allChars.shuffled(random).joinToString("")
    }

    /**
     * Calculate password strength as a score 0–4.
     * 0 = very weak, 1 = weak, 2 = fair, 3 = strong, 4 = very strong
     */
    fun calculateStrength(password: String): Int {
        if (password.isEmpty()) return 0

        var score = 0

        // Length scoring
        if (password.length >= 8) score++
        if (password.length >= 12) score++
        if (password.length >= 16) score++

        // Character diversity
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        val diversity = listOf(hasLower, hasUpper, hasDigit, hasSymbol).count { it }
        if (diversity >= 3) score++
        if (diversity >= 4) score++

        // Cap at 4
        return score.coerceAtMost(4)
    }

    fun strengthLabel(score: Int): String = when (score) {
        0 -> "Very Weak"
        1 -> "Weak"
        2 -> "Fair"
        3 -> "Strong"
        else -> "Very Strong"
    }
}
