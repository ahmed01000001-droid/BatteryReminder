package com.example.batteryreminder

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var enabledSwitch: Switch
    private lateinit var thresholdInput: EditText
    private lateinit var intervalInput: EditText
    private lateinit var ignoreChargingCheck: CheckBox
    private lateinit var currentBatteryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifier.ensureChannel(this)
        requestNotificationPermissionIfNeeded()
        buildUi()
        loadValues()
        refreshBatteryText()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(42, 54, 42, 42)
        }

        val title = TextView(this).apply {
            text = "Battery Reminder"
            textSize = 26f
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())

        currentBatteryText = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, 22, 0, 22)
        }
        root.addView(currentBatteryText, matchWrap())

        enabledSwitch = Switch(this).apply {
            text = "Enable low battery alerts"
            textSize = 18f
        }
        root.addView(enabledSwitch, matchWrap())

        root.addView(label("Battery threshold %"))
        thresholdInput = numberInput()
        root.addView(thresholdInput, matchWrap())

        root.addView(label("Repeat every minutes (minimum 15)"))
        intervalInput = numberInput()
        root.addView(intervalInput, matchWrap())

        ignoreChargingCheck = CheckBox(this).apply {
            text = "Do not alert while charging"
            textSize = 16f
        }
        root.addView(ignoreChargingCheck, matchWrap())

        val saveButton = Button(this).apply {
            text = "Save Settings"
            setOnClickListener { saveSettings() }
        }
        root.addView(saveButton, matchWrap())

        val testButton = Button(this).apply {
            text = "Check Now"
            setOnClickListener {
                WorkScheduler.runOnceNow(this@MainActivity)
                refreshBatteryText()
                Toast.makeText(this@MainActivity, "Battery checked", Toast.LENGTH_SHORT).show()
            }
        }
        root.addView(testButton, matchWrap())

        val note = TextView(this).apply {
            text = "Note: Android limits reliable periodic background checks to 15 minutes or more."
            textSize = 13f
            setPadding(0, 24, 0, 0)
        }
        root.addView(note, matchWrap())

        setContentView(root)
    }

    private fun loadValues() {
        enabledSwitch.isChecked = AppPrefs.isEnabled(this)
        thresholdInput.setText(AppPrefs.threshold(this).toString())
        intervalInput.setText(AppPrefs.intervalMinutes(this).toString())
        ignoreChargingCheck.isChecked = AppPrefs.ignoreCharging(this)
    }

    private fun saveSettings() {
        val threshold = thresholdInput.text.toString().toIntOrNull()?.coerceIn(1, 100) ?: 20
        val interval = intervalInput.text.toString().toLongOrNull()?.coerceAtLeast(15L) ?: 15L
        val enabled = enabledSwitch.isChecked
        val ignoreCharging = ignoreChargingCheck.isChecked

        thresholdInput.setText(threshold.toString())
        intervalInput.setText(interval.toString())

        AppPrefs.save(this, enabled, threshold, interval, ignoreCharging)
        if (enabled) WorkScheduler.schedule(this) else WorkScheduler.cancel(this)
        refreshBatteryText()

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
    }

    private fun refreshBatteryText() {
        val info = BatteryUtils.read(this)
        currentBatteryText.text = if (info == null) {
            "Current battery: unavailable"
        } else {
            val chargingText = if (info.isCharging) "charging" else "not charging"
            "Current battery: ${info.percent}% - $chargingText"
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 10)
        }
    }

    private fun label(textValue: String): TextView = TextView(this).apply {
        text = textValue
        textSize = 15f
        setPadding(0, 18, 0, 4)
    }

    private fun numberInput(): EditText = EditText(this).apply {
        inputType = InputType.TYPE_CLASS_NUMBER
        textSize = 18f
        setSingleLine(true)
    }

    private fun matchWrap(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
}
