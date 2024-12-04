package com.capstone.aiyam.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Patterns
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.net.URLConnection.*
import java.text.SimpleDateFormat
import java.time.format.TextStyle
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

fun String.parseDateTime(): String {
    val parsedDateTime = LocalDateTime.parse(this.removeSuffix("Z"))
    val instant = parsedDateTime.toInstant(TimeZone.UTC)
    val date = instant.toLocalDateTime(TimeZone.UTC).date
    val time = instant.toLocalDateTime(TimeZone.UTC).time

    val year = date.year.toString()
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    val formattedTime = "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}:${time.second.toString().padStart(2, '0')}"
    return "$dayOfWeek, $month $year $formattedTime"
}

@SuppressLint("DefaultLocale")
fun Long.toFormattedTime():String{
    val seconds = ((this / 1000) % 60).toInt()
    val minutes = ((this / (1000 * 60)) % 60).toInt()
    val hours = ((this / (1000 * 60 * 60)) % 24).toInt()

    return if (hours >0){
        String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }else{
        String.format("%02d:%02d",minutes,seconds)
    }
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

fun String.isValidPhoneNumber(): Boolean {
    // Regex to check if phone starts with 8 and contains 10-12 digits
    val regex = "^8\\d{9,11}$".toRegex()
    return regex.matches(this)
}

fun String.parseDateToEnglish(): String {
    val date = LocalDate.parse(this)

    val year = date.year.toString()
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    return "$dayOfWeek, $month $year"
}
