package com.freedom.crypto

import org.whispersystems.libsignal.SignalProtocolAddress
import org.whispersystems.libsignal.state.SessionRecord
import org.whispersystems.libsignal.state.SessionStore
import java.util.concurrent.ConcurrentHashMap

class InMemorySessionStore : SessionStore {

    // In production, use Encrypted Room or secure persistent storage
    private val sessionMap = ConcurrentHashMap<SignalProtocolAddress, SessionRecord>()

    override fun loadSession(address: SignalProtocolAddress): SessionRecord {
        return sessionMap[address]?.copy() ?: SessionRecord()
    }

    override fun getSubDeviceSessions(name: String): Collection<SignalProtocolAddress> {
        return sessionMap.keys.filter { it.name == name }
    }

    override fun storeSession(address: SignalProtocolAddress, record: SessionRecord) {
        sessionMap[address] = record.copy()
    }

    override fun containsSession(address: SignalProtocolAddress): Boolean {
        return sessionMap.containsKey(address)
    }

    override fun deleteSession(address: SignalProtocolAddress) {
        sessionMap.remove(address)
    }

    override fun deleteAllSessions(name: String) {
        val keysToRemove = sessionMap.keys.filter { it.name == name }
        for (key in keysToRemove) {
            sessionMap.remove(key)
        }
    }
}
