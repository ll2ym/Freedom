package com.freedom.crypto

import org.whispersystems.libsignal.InvalidKeyIdException
import org.whispersystems.libsignal.state.PreKeyRecord
import org.whispersystems.libsignal.state.PreKeyStore
import java.util.concurrent.ConcurrentHashMap

class InMemoryPreKeyStore : PreKeyStore {

    // In production, use secure persistent storage (e.g., EncryptedRoom)
    private val preKeyMap = ConcurrentHashMap<Int, PreKeyRecord>()

    override fun loadPreKey(preKeyId: Int): PreKeyRecord {
        return preKeyMap[preKeyId]
            ?: throw InvalidKeyIdException("No such pre-key with id: $preKeyId")
    }

    override fun storePreKey(preKeyId: Int, record: PreKeyRecord) {
        preKeyMap[preKeyId] = record
    }

    override fun containsPreKey(preKeyId: Int): Boolean {
        return preKeyMap.containsKey(preKeyId)
    }

    override fun removePreKey(preKeyId: Int) {
        if (!preKeyMap.containsKey(preKeyId)) {
            throw InvalidKeyIdException("Cannot remove non-existent pre-key with id: $preKeyId")
        }
        preKeyMap.remove(preKeyId)
    }
}
