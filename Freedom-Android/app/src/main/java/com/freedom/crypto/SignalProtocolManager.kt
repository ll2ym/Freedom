package com.freedom.crypto

import android.util.Base64
import org.whispersystems.libsignal.*
import org.whispersystems.libsignal.state.*
import org.whispersystems.libsignal.protocol.*
import org.whispersystems.libsignal.util.KeyHelper
import java.util.*

class SignalProtocolManager(
    private val currentUserId: String,
    private val deviceId: Int = 1
) {

    private val identityKeyPair: IdentityKeyPair = KeyHelper.generateIdentityKeyPair()
    private val registrationId: Int = KeyHelper.generateRegistrationId(false)

    private val identityKeyStore = InMemoryIdentityKeyStore(identityKeyPair, registrationId)
    private val preKeyStore = InMemoryPreKeyStore()
    private val signedPreKeyStore = InMemorySignedPreKeyStore()
    private val sessionStore = InMemorySessionStore()

    init {
        generateAndStorePreKeys()
    }

    private fun generateAndStorePreKeys() {
        val preKeys = KeyHelper.generatePreKeys(0, 100)
        preKeys.forEach { preKey ->
            preKeyStore.storePreKey(preKey.id, preKey)
        }

        val signedPreKeyId = 5
        val signedPreKey = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId)
        signedPreKeyStore.storeSignedPreKey(signedPreKeyId, signedPreKey)
    }

    fun getPreKeyBundle(): PreKeyBundle {
        val preKey = preKeyStore.loadPreKey(0)
        val signedPreKey = signedPreKeyStore.loadSignedPreKey(5)

        return PreKeyBundle(
            registrationId,
            deviceId,
            preKey.id, preKey.keyPair.publicKey,
            5, signedPreKey.keyPair.publicKey,
            signedPreKey.signature,
            identityKeyPair.publicKey
        )
    }

    fun encryptMessage(recipientId: String, preKeyBundle: PreKeyBundle, message: String): String {
        val address = SignalProtocolAddress(recipientId, deviceId)

        val sessionBuilder = SessionBuilder(sessionStore, preKeyStore, signedPreKeyStore, identityKeyStore)
        sessionBuilder.process(preKeyBundle)

        val cipher = SessionCipher(sessionStore, preKeyStore, signedPreKeyStore, identityKeyStore)
        val ciphertext = cipher.encrypt(message.toByteArray())

        return when (ciphertext.type) {
            CiphertextMessage.PREKEY_TYPE -> {
                Base64.encodeToString(ciphertext.serialize(), Base64.NO_WRAP)
            }
            else -> throw IllegalStateException("Unexpected ciphertext type")
        }
    }

    fun decryptMessage(senderId: String, messageBase64: String): String {
        val address = SignalProtocolAddress(senderId, deviceId)

        val messageBytes = Base64.decode(messageBase64, Base64.NO_WRAP)
        val cipher = SessionCipher(sessionStore, preKeyStore, signedPreKeyStore, identityKeyStore)

        return try {
            val message = PreKeySignalMessage(messageBytes)
            val plaintext = cipher.decrypt(message)
            String(plaintext)
        } catch (e: InvalidMessageException) {
            // Could also be SignalMessage if it's not a PreKey
            val message = SignalMessage(messageBytes)
            val plaintext = cipher.decrypt(message)
            String(plaintext)
        }
    }
}
