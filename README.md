# 🥊 Run Tyson
An Android dodge game where the player must avoid falling boxing gloves across 5 lanes while collecting coins — all while running from Tyson.

---

## 🎮 Game Modes

| Mode | Description |
|------|-------------|
| **Buttons – Slow** | Standard speed, control with on-screen left/right buttons |
| **Buttons – Fast** | Same controls, higher tick rate for extra challenge |
| **Sensor Mode** | Tilt your phone left/right to change lanes; tilt forward/back to speed up or slow down |

---

## 📦 Features

- 5-lane grid with gloves (obstacles) and coins (collectibles)
- 3 lives — lose one each time a glove reaches your lane
- Score measured in meters traveled
- Coin counter tracked separately
- Crash sound effect on collision
- Top-10 leaderboard saved locally via SharedPreferences (JSON)
- GPS location captured on game over and reverse-geocoded to city name
- Scoreboard screen with ranked list + Google Maps showing where each score was achieved
- Accelerometer-based tilt control (X axis = lane change, Y axis = speed)

---

## 🧱 Project Structure

```
com.example.hw1
│
├── GameManager.kt         # Game loop, 5×8 grid, spawn logic, collision detection
├── GameActivity.kt        # Main game screen, timer, button/sensor input
├── MenuActivity.kt        # Mode selection + location permission request
├── GameOverActivity.kt    # Score submission, GPS capture, reverse geocoding
├── HighScoresActivity.kt  # Hosts leaderboard + map fragments
│
├── LeaderboardFragment.kt # RecyclerView ranked list
├── LeaderboardAdapter.kt  # Adapter for score rows
├── MapFragment.kt         # Google Maps with score location markers
│
├── ScoreManager.kt        # Save/load top-10 scores via Gson + SharedPreferences
├── ScoreRecord.kt         # Data class: name, score, coins, lat, lng, locationName
├── GameMode.kt            # Enum: BUTTON_SLOW, BUTTON_FAST, SENSOR
│
├── TiltDetector.kt        # Accelerometer listener → lane change + speed callbacks
├── TiltCallback.kt        # Interface for tilt events
└── SignalManager.kt       # Toast + vibration utilities
```

---

## 🛠️ Tech Stack

- **Kotlin**
- **Android SDK**
- **RecyclerView** — leaderboard list
- **Gson** — JSON serialization of scores
- **SharedPreferences** — local score persistence
- **Sensors API** — `TYPE_ACCELEROMETER` for tilt control
- **Google Maps SDK** — score location display
- **FusedLocationProvider** — high-accuracy GPS on game over
- **Geocoder** — reverse geocoding lat/lng to city name
- **MediaPlayer** — crash sound effect
