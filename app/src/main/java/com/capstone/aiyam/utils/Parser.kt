package com.capstone.aiyam.utils

import android.os.Build
import android.util.Patterns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.net.URLConnection
import java.net.URLConnection.*
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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

@RequiresApi(Build.VERSION_CODES.O)
fun String.parseDateTime(): String {
    val zonedDateTime = ZonedDateTime.parse(this) // Parse the ISO 8601 timestamp
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss z") // Desired format
    val formattedDate = zonedDateTime.format(formatter) // Format the date
    return formattedDate
}

fun CharSequence.parseEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.parsePassword(): Boolean {
    return this.length >= 8 && this.any { it.isDigit() } && this.any { it.isLetter() } && this.any { !it.isLetterOrDigit() } && this.none { it.isWhitespace() } && this.none { !it.isLetterOrDigit() } && this.none { !it.isLetter() } && this.none { !it.isDigit() }
}

fun String.getMimeTypeFromUrl(): String? {
    return try {
        val connection = guessContentTypeFromName(this)
        connection ?: run {
            val extension = MimeTypeMap.getFileExtensionFromUrl(this)
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
    } catch (e: Exception) {
        null
    }
}
