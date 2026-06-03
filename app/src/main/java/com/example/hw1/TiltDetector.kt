package com.example.hw1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Wraps the accelerometer and translates tilt into game callbacks.
 *  - X axis: left/right lane changes (debounced)
 *  - Y axis: forward/back speed changes (continuous)
 * Call start() in onResume and stop() in onPause.
 */
class TiltDetector(context: Context, private val callback: TiltCallback) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTiltTime = 0L

    companion object {
        private const val TILT_THRESHOLD  = 3.0f   // m/s² to register a lane change
        private const val DEBOUNCE_MS     = 500L    // min ms between two lane changes

        // Y axis ≈ +9.8 when phone held upright portrait.
        // Tilting the top forward lowers Y toward 0.
        private const val SPEED_UP_Y   = 7.0f   // below this → speed up
        private const val SPEED_DOWN_Y = 8.5f   // above this → speed down
    }

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x   = event.values[0]   // positive = tilted left, negative = tilted right
        val y   = event.values[1]   // ~+9.8 upright; decreases when tilted forward
        val now = System.currentTimeMillis()

        // Lane changes with debounce
        if (now - lastTiltTime >= DEBOUNCE_MS) {
            when {
                x >= TILT_THRESHOLD  -> { callback.tiltLeft();  lastTiltTime = now }
                x <= -TILT_THRESHOLD -> { callback.tiltRight(); lastTiltTime = now }
            }
        }

        // Speed control – continuous, no debounce needed (calls are idempotent)
        when {
            y < SPEED_UP_Y   -> callback.tiltSpeedUp()
            y > SPEED_DOWN_Y -> callback.tiltSpeedDown()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
