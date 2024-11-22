package com.capstone.aiyam.utils

import android.annotation.SuppressLint
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
import java.util.TimeZone

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
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = inputFormat.parse(this)

    val outputFormat = SimpleDateFormat("MMMM dd, yyyy HH:mm:ss z", Locale.getDefault())
    outputFormat.timeZone = TimeZone.getTimeZone("UTC")

    val formattedDate = date?.let { outputFormat.format(it) } ?: this.parseDate() ?: this
    return formattedDate
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
