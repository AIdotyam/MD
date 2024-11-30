package com.capstone.aiyam.utils

import java.security.MessageDigest
import java.util.UUID

fun createNonce(): String {
    try {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
        return hashedNonce
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}
