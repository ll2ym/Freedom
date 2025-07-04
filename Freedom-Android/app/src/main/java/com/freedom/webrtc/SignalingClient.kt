package com.freedom.webrtc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import java.util.concurrent.TimeUnit

class SignalingClient(
    private val peerId: String,
    private val roomId: String,
    private val token: String,
    private val onSignalReceived: (from: String, data: Map<String, Any>) -> Unit
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    fun connect() {
        val url = "wss://your-server.com/ws/signaling/$roomId/$peerId?token=$token"

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("SignalingClient connected to $url")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val json = Json.decodeFromString<Map<String, Any>>(text)
                        val from = json["from"] as? String ?: return@launch
                        val data = json["data"] as? Map<String, Any> ?: return@launch
                        onSignalReceived(from, data)
                    } catch (e: Exception) {
                        println("Failed to parse signaling message: ${e.message}")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Signaling WebSocket error: ${t.localizedMessage}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, reason)
                println("Signaling WebSocket closing: $reason")
            }
        })
    }

    fun send(toPeerId: String, signalData: Map<String, Any>) {
        val payload = mapOf(
            "to" to toPeerId,
            "type" to "signal",
            "data" to signalData
        )
        val json = Json.encodeToString(payload)
        webSocket?.send(json)
    }

    fun close() {
        webSocket?.close(1000, "Closing")
    }
}
