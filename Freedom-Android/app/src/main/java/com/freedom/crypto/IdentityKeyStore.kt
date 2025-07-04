package com.freedom.crypto

import org.whispersystems.libsignal.IdentityKey
import org.whispersystems.libsignal.IdentityKeyPair
import org.whispersystems.libsignal.ecc.ECPublicKey
import org.whispersystems.libsignal.state.IdentityKeyStore

class InMemoryIdentityKeyStore(
    private val identityKeyPair: IdentityKeyPair,
    private val registrationId: Int
) : IdentityKeyStore {

    private val trustedKeys: MutableMap<String, IdentityKey> = mutableMapOf()

    override fun getIdentityKeyPair(): IdentityKeyPair {
        return identityKeyPair
    }

    override fun getLocalRegistrationId(): Int {
        return registrationId
    }

    override fun saveIdentity(identifier: SignalProtocolAddress, identityKey: IdentityKey): Boolean {
        val key = getMapKey(identifier)
        val existing = trustedKeys[key]
        val changed = existing == null || existing != identityKey
        trustedKeys[key] = identityKey
        return changed
    }

    override fun isTrustedIdentity(identifier: SignalProtocolAddress, identityKey: IdentityKey, direction: IdentityKeyStore.Direction): Boolean {
        val key = getMapKey(identifier)
        val trusted = trustedKeys[key]
        return trusted == null || trusted == identityKey
    }

    override fun getIdentity(identifier: SignalProtocolAddress): IdentityKey? {
        return trustedKeys[getMapKey(identifier)]
    }

    private fun getMapKey(identifier: SignalProtocolAddress): String {
        return "${identifier.name}:${identifier.deviceId}"
    }
}
