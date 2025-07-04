package com.freedom.data

data class MessageModel(
    val id: String,
    val text: String,
    val senderId: String,
    val receiverId: String,
    val timestamp: Long,
    val isEncrypted: Boolean = true,
    val isDelivered: Boolean = false,
    val isSeen: Boolean = false
)