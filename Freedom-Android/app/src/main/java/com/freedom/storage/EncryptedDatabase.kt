package com.freedom.storage

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

abstract class EncryptedDatabase : RoomDatabase() {
    // Add your DAOs here, for example:
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: EncryptedDatabase? = null

        fun getInstance(context: Context, passphrase: String): EncryptedDatabase {
            return INSTANCE ?: synchronized(this) {
                val factory = SupportFactory(passphrase.toByteArray())
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EncryptedDatabase::class.java,
                    "freedom_secure.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun generatePassphrase(username: String, salt: ByteArray): ByteArray {
            val keySpec = PBEKeySpec(username.toCharArray(), salt, 10000, 256)
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(keySpec).encoded
        }

        fun generateRandomSalt(): ByteArray {
            val salt = ByteArray(16)
            SecureRandom().nextBytes(salt)
            return salt
        }
    }
}
