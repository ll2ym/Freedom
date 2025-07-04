package com.freedom.crypto

import org.whispersystems.libsignal.InvalidKeyIdException
import org.whispersystems.libsignal.state.SignedPreKeyRecord
import org.whispersystems.libsignal.state.SignedPreKeyStore
import java.util.concurrent.ConcurrentHashMap

class InMemorySignedPreKeyStore : SignedPreKeyStore {

    // In-memory store — replace with secure persistent storage in production
    private val signedPreKeys = ConcurrentHashMap<Int, SignedPreKeyRecord>()

    override fun loadSignedPreKey(signedPreKeyId: Int): SignedPreKeyRecord {
        return signedPreKeys[signedPreKeyId]
            ?: throw InvalidKeyIdException("SignedPreKey ID $signedPreKeyId not found.")
    }

    override fun loadSignedPreKeys(): Collection<SignedPreKeyRecord> {
        return signedPreKeys.values
    }

    override fun storeSignedPreKey(signedPreKeyId: Int, record: SignedPreKeyRecord) {
        signedPreKeys[signedPreKeyId] = record
    }

    override fun containsSignedPreKey(signedPreKeyId: Int): Boolean {
        return signedPreKeys.containsKey(signedPreKeyId)
    }

    override fun removeSignedPreKey(signedPreKeyId: Int) {
        if (!signedPreKeys.containsKey(signedPreKeyId)) {
            throw InvalidKeyIdException("Cannot remove: SignedPreKey ID $signedPreKeyId does not exist.")
        }
        signedPreKeys.remove(signedPreKeyId)
    }
}
