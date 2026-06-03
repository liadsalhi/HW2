package com.example.hw1

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast

/**
 * Singleton that handles vibration, toasts, sounds.
 * Crash sound uses MediaPlayer with res/raw/crash_sound.wav.
 * Coin sound uses ToneGenerator (no file needed).
 * Call release() when the activity is destroyed.
 */
object SignalManager {

    private const val VIBRATION_MS   = 400L
    private const val COIN_VOLUME    = 60
    private const val COIN_TONE_MS   = 80

    private var coinToneGenerator: ToneGenerator? = null

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun vibrate(context: Context) {
        val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_MS, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_MS)
        }
    }

    /** Plays crash_sound.wav via MediaPlayer. Releases itself when done. */
    fun playCrashSound(context: Context) {
        try {
            val mp = MediaPlayer.create(context, R.raw.crash_sound)
            mp?.setOnCompletionListener { it.release() }
            mp?.start()
        } catch (_: Exception) { }
    }

    /** Short high-pitched beep when a coin is collected. */
    fun playCoinSound() {
        try {
            if (coinToneGenerator == null) {
                coinToneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, COIN_VOLUME)
            }
            coinToneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, COIN_TONE_MS)
        } catch (_: Exception) { }
    }

    /** Release audio resources – call from Activity.onDestroy(). */
    fun release() {
        coinToneGenerator?.release()
        coinToneGenerator = null
    }
}
