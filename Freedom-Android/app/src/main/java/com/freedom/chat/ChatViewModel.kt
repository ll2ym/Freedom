package com.freedom.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freedom.network.WebSocketManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val gson: Gson
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        viewModelScope.launch {
            webSocketManager.connect("your_auth_token") // Replace with actual token
            webSocketManager.messages.collect { messageJson ->
                val message = gson.fromJson(messageJson, Message::class.java)
                _messages.value = _messages.value + message
            }
        }
    }

    fun sendMessage(text: String, to: String) {
        viewModelScope.launch {
            val message = mapOf("to" to to, "text" to text)
            webSocketManager.sendMessage(gson.toJson(message))
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}

data class Message(
    val from: String,
    val to: String,
    val text: String,
    val timestamp: Long
)
