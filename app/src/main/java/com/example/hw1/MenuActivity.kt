package com.example.hw1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Entry point of the app.
 * Three buttons let the player choose: Buttons-Slow, Buttons-Fast, or Sensor Mode.
 * In Sensor Mode the speed is controlled in-game by tilting the phone forward/back.
 */
class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

        findViewById<Button>(R.id.btn_buttons_slow).setOnClickListener {
            startGame(GameMode.BUTTON_SLOW)
        }
        findViewById<Button>(R.id.btn_buttons_fast).setOnClickListener {
            startGame(GameMode.BUTTON_FAST)
        }
        findViewById<Button>(R.id.btn_sensor_mode).setOnClickListener {
            startGame(GameMode.SENSOR)
        }
        findViewById<Button>(R.id.btn_high_scores).setOnClickListener {
            startActivity(Intent(this, HighScoresActivity::class.java))
        }
    }

    private fun startGame(mode: GameMode) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(GameActivity.EXTRA_MODE, mode.name)
        startActivity(intent)
    }
}
