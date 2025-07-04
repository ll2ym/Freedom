package com.freedom.data.model

data class Message(
    val id: String,
    val text: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEncrypted: Boolean = true,
    val isDelivered: Boolean = false,
    val isSeen: Boolean = false
)