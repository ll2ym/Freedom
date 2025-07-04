package com.freedom.storage

import android.content.Context
import androidx.room.*
import com.freedom.data.Message
import com.freedom.data.User
import com.freedom.data.Group
import com.freedom.utils.Converters
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Message::class, User::class, Group::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FreedomDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao

    companion object {
        @Volatile
        private var INSTANCE: FreedomDatabase? = null

        fun getInstance(context: Context, passphrase: String): FreedomDatabase {
            return INSTANCE ?: synchronized(this) {
                val factory = SupportFactory(passphrase.toByteArray())
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FreedomDatabase::class.java,
                    "freedom_secure.db"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}