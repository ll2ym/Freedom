package com.freedom.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.data.ChatRepository
import com.freedom.data.MessageModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val repository: ChatRepository,
    val chatPartnerId: String,
    private val currentUserId: String
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages: StateFlow<List<MessageModel>> = _messages

    init {
        observeMessages()
    }

    private fun observeMessages() {
        viewModelScope.launch {
            repository.getMessages(chatPartnerId).collect { newMessages ->
                _messages.value = newMessages.sortedBy { it.timestamp }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val message = MessageModel(
            id = UUID.randomUUID().toString(),
            text = text,
            senderId = currentUserId,
            receiverId = chatPartnerId,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.sendMessage(message)
        }
    }
}
