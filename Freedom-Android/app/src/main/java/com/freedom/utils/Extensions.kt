package com.freedom.utils

import android.util.Base64
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Format timestamps to readable time
fun Long.toReadableTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

// Launch coroutine on IO thread safely
fun CoroutineScope.launchIO(block: suspend () -> Unit) {
    this.launch(Dispatchers.IO) {
        try {
            block()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// Convert ByteArray to Base64 string
fun ByteArray.toBase64(): String =
    Base64.encodeToString(this, Base64.NO_WRAP)

// Convert Base64 string to ByteArray
fun String.fromBase64(): ByteArray =
    Base64.decode(this, Base64.NO_WRAP)
