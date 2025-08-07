package com.freedom.network

import com.freedom.utils.Constants
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: HttpClient
) {
    private var session: DefaultClientWebSocketSession? = null

    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    suspend fun connect(token: String) {
        try {
            session = client.webSocketSession {
                url(Constants.CHAT_SOCKET_URL + "?token=$token")
            }
            for (frame in session!!.incoming) {
                if (frame is Frame.Text) {
                    _messages.emit(frame.readText())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun sendMessage(message: String) {
        session?.send(message)
    }

    fun disconnect() {
        session?.cancel()
        session = null
    }
}
