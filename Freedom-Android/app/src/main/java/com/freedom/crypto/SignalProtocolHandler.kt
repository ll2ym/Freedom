package com.freedom.crypto

import org.whispersystems.libsignal.*
import org.whispersystems.libsignal.state.*
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore
import org.whispersystems.libsignal.util.KeyHelper
import java.util.*

class SignalProtocolHandler(
    private val username: String,
    private val deviceId: Int = 1
) {

    private val identityKeyPair: IdentityKeyPair = KeyHelper.generateIdentityKeyPair()
    private val registrationId: Int = KeyHelper.generateRegistrationId(false)

    private val preKeys: List<PreKeyRecord>
    private val signedPreKey: SignedPreKeyRecord
    private val store: SignalProtocolStore

    init {
        preKeys = KeyHelper.generatePreKeys(0, 100)
        signedPreKey = KeyHelper.generateSignedPreKey(identityKeyPair, 0)
        store = InMemorySignalProtocolStore(identityKeyPair, registrationId)

        // Save prekeys and signed prekey to store
        preKeys.forEach { store.storePreKey(it.id, it) }
        store.storeSignedPreKey(signedPreKey.id, signedPreKey)
    }

    fun getPreKeyBundle(): PreKeyBundle {
        return PreKeyBundle(
            registrationId,
            deviceId,
            preKeys[0].id,
            preKeys[0].keyPair.publicKey,
            signedPreKey.id,
            signedPreKey.keyPair.publicKey,
            signedPreKey.signature,
            identityKeyPair.publicKey
        )
    }

    fun encryptMessage(toUserId: String, toDeviceId: Int, preKeyBundle: PreKeyBundle, plaintext: String): CiphertextMessage {
        val remoteAddress = SignalProtocolAddress(toUserId, toDeviceId)
        val sessionBuilder = SessionBuilder(store, remoteAddress)
        sessionBuilder.process(preKeyBundle)

        val sessionCipher = SessionCipher(store, remoteAddress)
        return sessionCipher.encrypt(plaintext.toByteArray())
    }

    fun decryptMessage(fromUserId: String, fromDeviceId: Int, message: CiphertextMessage): String {
        val remoteAddress = SignalProtocolAddress(fromUserId, fromDeviceId)
        val sessionCipher = SessionCipher(store, remoteAddress)

        val plaintext = when (message.type) {
            CiphertextMessage.PREKEY_TYPE -> sessionCipher.decrypt(PreKeySignalMessage(message.serialize()))
            CiphertextMessage.WHISPER_TYPE -> sessionCipher.decrypt(SignalMessage(message.serialize()))
            else -> throw IllegalArgumentException("Unknown message type")
        }

        return String(plaintext)
    }
}
