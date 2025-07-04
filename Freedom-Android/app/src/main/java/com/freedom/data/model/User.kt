package com.freedom.data.model

data class User(
    val id: String,
    val username: String,
    val displayName: String = username,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)