package com.capstone.aiyam.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Locale

fun String.parseDate(): String? {
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormatter.parse(this) ?: return null

        val outputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormatter.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun CharSequence.parseEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.parsePassword(): Boolean {
    return this.length >= 8 && this.any { it.isDigit() } && this.any { it.isLetter() } && this.any { !it.isLetterOrDigit() } && this.none { it.isWhitespace() } && this.none { !it.isLetterOrDigit() } && this.none { !it.isLetter() } && this.none { !it.isDigit() }
}