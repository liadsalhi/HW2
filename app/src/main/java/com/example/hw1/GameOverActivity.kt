package com.example.hw1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.OnBackPressedCallback
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

/**
 * Shown when the player loses all lives.
 * Receives score (meters) and coins from GameActivity, asks for a name,
 * saves the result with GPS coordinates and opens HighScoresActivity.
 */
class GameOverActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_COINS = "extra_coins"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val coins = intent.getIntExtra(EXTRA_COINS, 0)

        val txtScore: TextView = findViewById(R.id.txt_game_over_score)
        val etName: EditText   = findViewById(R.id.et_player_name)
        val btnSend: Button    = findViewById(R.id.btn_send_score)

        txtScore.text = "Score: $score m  |  Coins: $coins"

        btnSend.setOnClickListener {
            val name = etName.text.toString().trim().ifBlank { "Unknown" }
            saveScoreAndContinue(name, score, coins)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@GameOverActivity, MenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        })
    }

    private fun saveScoreAndContinue(name: String, score: Int, coins: Int) {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            persist(ScoreRecord(name, score, 0.0, 0.0, "Unknown", coins))
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(this)
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        fused.getCurrentLocation(request, null)
            .addOnSuccessListener { loc ->
                val lat = loc?.latitude ?: 0.0
                val lng = loc?.longitude ?: 0.0
                if (loc != null) {
                    reverseGeocode(lat, lng) { locName ->
                        persist(ScoreRecord(name, score, lat, lng, locName, coins))
                    }
                } else {
                    persist(ScoreRecord(name, score, 0.0, 0.0, "Unknown", coins))
                }
            }
            .addOnFailureListener {
                persist(ScoreRecord(name, score, 0.0, 0.0, "Unknown", coins))
            }
    }

    private fun reverseGeocode(lat: Double, lng: Double, onResult: (String) -> Unit) {
        val geocoder = Geocoder(this, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lng, 1) { addresses ->
                val name = addresses.firstOrNull()?.let {
                    it.locality ?: it.adminArea ?: it.countryName
                } ?: "Unknown"
                onResult(name)
            }
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val name = addresses?.firstOrNull()?.let {
                    it.locality ?: it.adminArea ?: it.countryName
                } ?: "Unknown"
                onResult(name)
            } catch (_: Exception) {
                onResult("Unknown")
            }
        }
    }

    private fun persist(record: ScoreRecord) {
        ScoreManager.saveScore(this, record)
        val intent = Intent(this, HighScoresActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

}