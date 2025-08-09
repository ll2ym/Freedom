package com.freedom

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.freedom.workers.DataWipeWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FreedomApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupDataWipeWorker()
    }

    private fun setupDataWipeWorker() {
        val workRequest = PeriodicWorkRequestBuilder<DataWipeWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
