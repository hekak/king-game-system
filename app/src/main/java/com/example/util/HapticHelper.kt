package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticHelper(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun spinTick() {
        vibrate(30, 40)
    }

    fun reelStop() {
        vibrate(45, 80)
    }

    fun comboHit() {
        vibrate(100, 160)
    }

    fun wildTransformation() {
        vibrate(120, 220)
    }

    fun bigWin() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 100, 80, 120, 80, 200)
                val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 100, 80, 120, 80, 200), -1)
            }
        } catch (_: Exception) {}
    }

    private fun vibrate(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }
}
