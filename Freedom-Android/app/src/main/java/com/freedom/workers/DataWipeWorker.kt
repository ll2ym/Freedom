package com.freedom.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.freedom.storage.SecurePrefs
import java.util.concurrent.TimeUnit

class DataWipeWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val securePrefs: SecurePrefs
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val wipeAfterLogoutStr = securePrefs.getString("wipe_after_logout", "00:00:00") ?: "00:00:00"
        val wipeAfterNoConnectionStr = securePrefs.getString("wipe_after_no_connection", "00:00:00") ?: "00:00:00"

        val wipeAfterLogoutMillis = parseTimeToMillis(wipeAfterLogoutStr)
        val wipeAfterNoConnectionMillis = parseTimeToMillis(wipeAfterNoConnectionStr)

        val lastLoginTime = securePrefs.getLong("last_login_time", 0)
        val lastConnectionTime = securePrefs.getLong("last_connection_time", 0)

        val currentTime = System.currentTimeMillis()

        if (wipeAfterLogoutMillis > 0 && currentTime - lastLoginTime > wipeAfterLogoutMillis) {
            wipeData()
            return Result.success()
        }

        if (wipeAfterNoConnectionMillis > 0 && currentTime - lastConnectionTime > wipeAfterNoConnectionMillis) {
            wipeData()
            return Result.success()
        }

        return Result.success()
    }

    private fun parseTimeToMillis(timeStr: String): Long {
        val parts = timeStr.split(":").map { it.toLongOrNull() ?: 0 }
        if (parts.size != 3) return 0
        val (days, hours, minutes) = parts
        return TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes)
    }

    private fun wipeData() {
        // Here you would wipe the database and other sensitive data.
        // For now, we will just clear the secure prefs.
        securePrefs.clear()
    }
}
