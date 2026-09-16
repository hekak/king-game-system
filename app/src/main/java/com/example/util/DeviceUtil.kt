package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.util.UUID

object DeviceUtil {

    private const val PREFS_NAME = "device_identity_prefs"
    private const val KEY_UNIQUE_DEVICE_ID = "unique_device_uuid"

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedUuid = prefs.getString(KEY_UNIQUE_DEVICE_ID, null)
        if (!savedUuid.isNullOrBlank()) {
            return savedUuid
        }

        // Try getting Android ID
        val androidId = try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            null
        }

        val finalId = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
            androidId.lowercase()
        } else {
            // Generate permanent UUID
            "dev_" + UUID.randomUUID().toString().replace("-", "").take(16)
        }

        prefs.edit().putString(KEY_UNIQUE_DEVICE_ID, finalId).apply()
        return finalId
    }

    fun getDeviceModel(): String {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val model = Build.MODEL
        return if (model.startsWith(manufacturer, ignoreCase = true)) {
            model
        } else {
            "$manufacturer $model"
        }
    }
}
