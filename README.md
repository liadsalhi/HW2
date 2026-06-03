# Run Tyson
An Android dodge game where the player avoids falling boxing gloves across 5 lanes while collecting coins.

## Game Modes

| Mode | Description |
|------|-------------|
| Buttons - Slow | Standard speed, left/right buttons |
| Buttons - Fast | Faster gameplay, same controls |
| Sensor Mode | Tilt phone left/right to change lanes, forward/back to change speed |

## Features

- 5 lanes with gloves (obstacles) and coins (collectibles)
- 3 lives
- Score in meters
- Coin counter
- Crash sound on collision
- Top-10 leaderboard saved locally
- GPS location saved on game over
- Scoreboard with ranking list and Google Maps
- Accelerometer tilt control

## Project Structure

```
com.example.hw1

GameManager.kt         - Game loop, grid logic, collision detection
GameActivity.kt        - Main game screen, input handling
MenuActivity.kt        - Mode selection
GameOverActivity.kt    - Score submission, GPS
HighScoresActivity.kt  - Leaderboard + map screen

LeaderboardFragment.kt - Score list (RecyclerView)
LeaderboardAdapter.kt  - List adapter
MapFragment.kt         - Google Maps with score markers

ScoreManager.kt        - Save/load scores with Gson
ScoreRecord.kt         - Data class: name, score, coins, location
GameMode.kt            - Enum: BUTTON_SLOW, BUTTON_FAST, SENSOR

TiltDetector.kt        - Accelerometer input
TiltCallback.kt        - Tilt event interface
SignalManager.kt       - Toast and vibration
```

## Tech Stack

- Kotlin
- Android SDK
- RecyclerView
- Gson
- SharedPreferences
- Sensors API (Accelerometer)
- Google Maps SDK
- FusedLocationProvider
- Geocoder
- MediaPlayer
