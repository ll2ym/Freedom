package com.freedom.storage

import androidx.room.*
import com.freedom.data.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE (senderUsername = :username OR receiverUsername = :username) AND groupId IS NULL ORDER BY timestamp ASC")
    fun getMessagesWithUser(username: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY timestamp ASC")
    fun getGroupMessages(groupId: String): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<Message>)

    @Update
    suspend fun update(message: Message)

    @Delete
    suspend fun delete(message: Message)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteById(messageId: Long)

    @Query("UPDATE messages SET isSeen = 1 WHERE id = :messageId")
    suspend fun markAsSeen(messageId: Long)

    @Query("UPDATE messages SET isDelivered = 1 WHERE id = :messageId")
    suspend fun markAsDelivered(messageId: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE receiverUsername = :username AND isSeen = 0")
    suspend fun getUnreadCount(username: String): Int
}