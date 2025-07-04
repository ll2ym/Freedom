package com.freedom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    val senderUsername: String,           // Who sent the message
    val receiverUsername: String?,        // For 1:1 chats (null if group message)
    val groupId: String? = null,          // For group messages (null if 1:1)

    val content: String,                  // Encrypted or plaintext content
    val timestamp: Long = System.currentTimeMillis(),

    val isIncoming: Boolean = true,       // Whether this message was received (vs. sent)
    val isEncrypted: Boolean = true,      // Whether message is encrypted
    val isDelivered: Boolean = false,     // Delivery acknowledgement
    val isSeen: Boolean = false           // Read status
)
