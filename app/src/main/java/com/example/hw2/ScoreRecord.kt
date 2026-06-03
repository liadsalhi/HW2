package com.example.hw2

/**
 * One entry in the leaderboard.
 * Stored as JSON in SharedPreferences via ScoreManager.
 */
data class ScoreRecord(
    val name: String,
    val score: Int,       // meters driven
    val lat: Double,
    val lng: Double,
    val locationName: String = "Unknown",
    val coins: Int = 0
)