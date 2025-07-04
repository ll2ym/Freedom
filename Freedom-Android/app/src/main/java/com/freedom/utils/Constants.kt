package com.freedom.utils

object Constants {

    // Server API base URL
    const val BASE_URL = "https://your-secure-server.com/api"

    // WebSocket endpoints
    const val CHAT_SOCKET_URL = "wss://your-secure-server.com/ws/chat"
    const val CALL_SOCKET_URL = "wss://your-secure-server.com/ws/call"

    // Encryption
    const val ENCRYPTION_ENABLED = true

    // Signal Protocol settings
    const val IDENTITY_KEY_ALIAS = "identity_key_pair"
    const val PRE_KEY_COUNT = 100
    const val PRE_KEY_BATCH_SIZE = 20

    // SecurePrefs keys
    const val PREF_AUTH_TOKEN = "auth_token"
    const val PREF_USERNAME = "username"
    const val PREF_SALT = "salt"
    const val PREF_REGISTRATION_ID = "registration_id"
    const val PREF_IDENTITY_KEY_PAIR = "identity_key_pair"

    // Database
    const val DATABASE_NAME = "freedom_secure.db"

    // Intent extras
    const val EXTRA_USERNAME = "extra_username"
    const val EXTRA_GROUP_ID = "extra_group_id"

    // Group limitations
    const val MAX_GROUP_MEMBERS = 40

    // UI/UX
    const val DEFAULT_AVATAR_URL = "https://yourcdn.com/default_avatar.png"
    const val APP_NAME = "Freedom"
    const val APP_SLOGAN = "It is human right for secure communications!"

    // WebRTC
    const val STUN_SERVER = "stun:stun.l.google.com:19302"
    const val TURN_SERVER = "turn:your-turn-server.com"
    const val TURN_USERNAME = "freedom"
    const val TURN_PASSWORD = "secureTurnPassword"

    // Logging
    const val LOG_TAG = "FreedomApp"
}
