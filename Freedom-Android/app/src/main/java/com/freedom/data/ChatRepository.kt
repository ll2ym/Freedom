package com.freedom.data

import com.freedom.crypto.SessionManager
import com.freedom.storage.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.whispersystems.libsignal.protocol.CiphertextMessage

class ChatRepository(
    private val messageDao: MessageDao,
    private val sessionManager: SessionManager,
    private val localUsername: String
) {

    suspend fun sendMessage(
        toUser: String,
        toDeviceId: Int = 1,
        plaintext: String
    ): Message = withContext(Dispatchers.IO) {
        val encrypted: CiphertextMessage = sessionManager.encryptMessage(toUser, toDeviceId, plaintext)

        val message = Message(
            senderUsername = localUsername,
            receiverUsername = toUser,
            groupId = null,
            content = encrypted.serialize().decodeToString(),
            isIncoming = false,
            isEncrypted = true,
            isDelivered = false,
            isSeen = false
        )

        messageDao.insert(message)
        message
    }

    suspend fun receiveMessage(
        fromUser: String,
        fromDeviceId: Int = 1,
        encryptedMessage: String
    ): Message = withContext(Dispatchers.IO) {
        val decryptedText = sessionManager.decryptMessage(
            fromUser,
            fromDeviceId,
            CiphertextMessage.fromSerialized(encryptedMessage.toByteArray())
        )

        val message = Message(
            senderUsername = fromUser,
            receiverUsername = localUsername,
            groupId = null,
            content = decryptedText,
            isIncoming = true,
            isEncrypted = true,
            isDelivered = true,
            isSeen = false
        )

        messageDao.insert(message)
        message
    }

    suspend fun getChatWithUser(username: String): List<Message> {
        return messageDao.getMessagesWithUser(username)
    }

    suspend fun markMessageAsSeen(messageId: Long) {
        val all = messageDao.getAllMessages().toMutableList()
        val index = all.indexOfFirst { it.id == messageId }
        if (index != -1) {
            val msg = all[index].copy(isSeen = true)
            messageDao.insert(msg)
        }
    }
}
