package com.example.batteryreminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class BatteryCheckWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext

        if (!AppPrefs.isEnabled(context)) {
            AppPrefs.resetLastNotifiedPercent(context)
            return Result.success()
        }

        val info = BatteryUtils.read(context) ?: return Result.retry()

        val threshold = AppPrefs.threshold(context)
        val ignoreCharging = AppPrefs.ignoreCharging(context)

        val isBlockedByCharging = ignoreCharging && info.isCharging

        if (isBlockedByCharging || info.percent > threshold) {
            AppPrefs.resetLastNotifiedPercent(context)
            return Result.success()
        }

        val lastNotifiedPercent = AppPrefs.lastNotifiedPercent(context)

        val shouldNotify =
            info.percent <= threshold &&
            info.percent != lastNotifiedPercent

        if (shouldNotify) {
            Notifier.showLowBattery(context, info.percent, threshold)
            AppPrefs.setLastNotifiedPercent(context, info.percent)
        }

        return Result.success()
    }
}
