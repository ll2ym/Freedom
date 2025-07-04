package com.freedom.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.freedom.utils.Converters

@Entity(tableName = "groups")
@TypeConverters(Converters::class)
data class Group(
    @PrimaryKey val groupId: String,               // Unique group identifier
    val name: String,                              // Display name of the group
    val members: List<String>,                     // List of usernames in the group
    val avatarUrl: String? = null,                 // Optional group image
    val createdBy: String,                         // Username of group creator
    val createdAt: Long = System.currentTimeMillis()  // Timestamp of group creation
)
