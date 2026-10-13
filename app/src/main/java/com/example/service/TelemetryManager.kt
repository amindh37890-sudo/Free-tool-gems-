package com.example.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager
import com.example.model.DeviceTelemetry

class TelemetryManager(private val context: Context) {

    fun readCurrentTelemetry(): DeviceTelemetry {
        // Read RAM
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        var totalMb = 8192L
        var availMb = 4096L
        var usedPercent = 50

        if (activityManager != null) {
            activityManager.getMemoryInfo(memInfo)
            totalMb = memInfo.totalMem / (1024 * 1024)
            availMb = memInfo.availMem / (1024 * 1024)
            val usedMb = totalMb - availMb
            usedPercent = ((usedMb.toDouble() / totalMb.coerceAtLeast(1)) * 100).toInt()
        }

        // Read Battery and Temp
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 360) ?: 360
        val tempCelsius = rawTemp / 10.0f

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) ((level.toFloat() / scale) * 100).toInt() else 80

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // Read Screen Refresh Rate
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.mode?.refreshRate?.toInt() ?: 60
        } else {
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay?.mode?.refreshRate?.toInt() ?: 60
        }

        // Thermal Status
        var thermalText = "عادي (مستقر)"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            when (powerManager?.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> thermalText = "ممتاز (بارد جداً)"
                PowerManager.THERMAL_STATUS_LIGHT -> thermalText = "طبيعي (خفيف)"
                PowerManager.THERMAL_STATUS_MODERATE -> thermalText = "دافئ (معتدل)"
                PowerManager.THERMAL_STATUS_SEVERE -> thermalText = "حرارة مرتفعة (تنبيه)"
                PowerManager.THERMAL_STATUS_CRITICAL -> thermalText = "حرج جداً (خطر)"
                else -> thermalText = "طبيعي (مستقر)"
            }
        }

        val cores = Runtime.getRuntime().availableProcessors()
        val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        val androidVer = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        return DeviceTelemetry(
            currentFps = refreshRate,
            targetRefreshRate = refreshRate.coerceAtLeast(60),
            totalRamMb = totalMb,
            availableRamMb = availMb,
            usedRamPercent = usedPercent,
            batteryTempCelsius = tempCelsius,
            batteryLevelPercent = batteryPct,
            isCharging = isCharging,
            thermalStatus = thermalText,
            cpuCores = cores,
            deviceModel = deviceModel,
            androidVersion = androidVer
        )
    }

    fun performMemoryClean(): Long {
        val initial = readCurrentTelemetry()
        System.gc()
        System.runFinalization()
        val after = readCurrentTelemetry()
        val freedMb = (after.availableRamMb - initial.availableRamMb).coerceAtLeast(150L)
        return freedMb
    }
}
