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
        if (!AppPrefs.isEnabled(context)) return Result.success()

        val info = BatteryUtils.read(context) ?: return Result.retry()
        val threshold = AppPrefs.threshold(context)
        val ignoreCharging = AppPrefs.ignoreCharging(context)

        val shouldNotify = info.percent <= threshold && !(ignoreCharging && info.isCharging)
        if (shouldNotify) {
            Notifier.showLowBattery(context, info.percent, threshold)
        }
        return Result.success()
    }
}
