package com.example.batteryreminder

import android.content.Context

object AppPrefs {
    private const val PREFS = "battery_reminder_prefs"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_THRESHOLD = "threshold"
    private const val KEY_INTERVAL = "interval_minutes"
    private const val KEY_IGNORE_CHARGING = "ignore_charging"

    fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)
    fun threshold(context: Context): Int = prefs(context).getInt(KEY_THRESHOLD, 20)
    fun intervalMinutes(context: Context): Long = prefs(context).getLong(KEY_INTERVAL, 15L)
    fun ignoreCharging(context: Context): Boolean = prefs(context).getBoolean(KEY_IGNORE_CHARGING, true)

    fun save(context: Context, enabled: Boolean, threshold: Int, intervalMinutes: Long, ignoreCharging: Boolean) {
        prefs(context).edit()
            .putBoolean(KEY_ENABLED, enabled)
            .putInt(KEY_THRESHOLD, threshold.coerceIn(1, 100))
            .putLong(KEY_INTERVAL, intervalMinutes.coerceAtLeast(15L))
            .putBoolean(KEY_IGNORE_CHARGING, ignoreCharging)
            .apply()
    }
}
