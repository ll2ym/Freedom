package com.freedom.crypto

import org.whispersystems.libsignal.*
import org.whispersystems.libsignal.protocol.CiphertextMessage
import org.whispersystems.libsignal.protocol.PreKeySignalMessage
import org.whispersystems.libsignal.protocol.SignalMessage
import org.whispersystems.libsignal.state.PreKeyBundle
import org.whispersystems.libsignal.state.SignalProtocolStore
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import org.whispersystems.libsignal.util.KeyHelper
import java.util.*

class SessionManager(
    private val store: SignalProtocolStore,
    private val username: String,
    private val deviceId: Int = 1
) {

    fun processPreKeyBundle(
        fromUserId: String,
        fromDeviceId: Int,
        preKeyBundle: PreKeyBundle
    ) {
        val address = SignalProtocolAddress(fromUserId, fromDeviceId)
        val builder = SessionBuilder(store, address)
        builder.process(preKeyBundle)
    }

    fun encryptMessage(
        toUserId: String,
        toDeviceId: Int,
        plaintext: String
    ): CiphertextMessage {
        val address = SignalProtocolAddress(toUserId, toDeviceId)
        val cipher = SessionCipher(store, address)
        return cipher.encrypt(plaintext.toByteArray())
    }

    fun decryptMessage(
        fromUserId: String,
        fromDeviceId: Int,
        message: CiphertextMessage
    ): String {
        val address = SignalProtocolAddress(fromUserId, fromDeviceId)
        val cipher = SessionCipher(store, address)

        val plaintextBytes = when (message.type) {
            CiphertextMessage.PREKEY_TYPE -> cipher.decrypt(PreKeySignalMessage(message.serialize()))
            CiphertextMessage.WHISPER_TYPE -> cipher.decrypt(SignalMessage(message.serialize()))
            else -> throw IllegalArgumentException("Unknown message type: ${message.type}")
        }

        return String(plaintextBytes)
    }

    companion object {
        fun createInMemory(username: String, deviceId: Int = 1): SessionManager {
            val identityKeyPair = KeyHelper.generateIdentityKeyPair()
            val registrationId = KeyHelper.generateRegistrationId(false)
            val store = InMemorySignalProtocolStore(identityKeyPair, registrationId)
            return SessionManager(store, username, deviceId)
        }
    }
}
