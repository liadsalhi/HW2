package com.example.hw1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Shows the leaderboard (top) and map (bottom) in a vertically split layout.
 * Implements OnScoreSelectedListener to forward row taps to MapFragment.
 */
class HighScoresActivity : AppCompatActivity(), LeaderboardFragment.OnScoreSelectedListener {

    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        if (savedInstanceState == null) {
            mapFragment = MapFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container_leaderboard, LeaderboardFragment())
                .replace(R.id.container_map, mapFragment)
                .commit()
        } else {
            mapFragment = supportFragmentManager
                .findFragmentById(R.id.container_map) as MapFragment
        }

        findViewById<Button>(R.id.btn_back_menu).setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onScoreSelected(record: ScoreRecord) {
        mapFragment.panToLocation(record.lat, record.lng)
    }
}
