package com.freedom.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,         // Unique user identifier (used instead of phone number)
    val displayName: String,                  // Name shown in UI
    val avatarUrl: String? = null,            // Optional avatar image
    val isTrusted: Boolean = false,           // Whether the user identity is verified/trusted
    val lastSeen: Long = System.currentTimeMillis(), // Timestamp of last activity
)
