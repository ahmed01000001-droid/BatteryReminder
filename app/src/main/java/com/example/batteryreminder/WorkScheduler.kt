package com.example.batteryreminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val PERIODIC_WORK_NAME = "battery_periodic_check"
    private const val IMMEDIATE_WORK_NAME = "battery_immediate_check"

    fun schedule(context: Context) {
        if (!AppPrefs.isEnabled(context)) {
            cancel(context)
            return
        }

        val interval = AppPrefs.intervalMinutes(context).coerceAtLeast(15L)
        val request = PeriodicWorkRequestBuilder<BatteryCheckWorker>(interval, TimeUnit.MINUTES).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        runOnceNow(context)
    }

    fun runOnceNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<BatteryCheckWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(IMMEDIATE_WORK_NAME)
    }
}
