# 🥊 Run Tyson
   2 An Android dodge game where the player must avoid falling boxing gloves across 5 lanes while collecting coins — all while running from Tyson.
   3
   4 ---
   5
   6 ## 🎮 Game Modes
   7
   8 | Mode | Description |
   9 |------|-------------|
  10 | **Buttons – Slow** | Standard speed, control with on-screen left/right buttons |
  11 | **Buttons – Fast** | Same controls, higher tick rate for extra challenge |
  12 | **Sensor Mode** | Tilt your phone left/right to change lanes; tilt forward/back to speed up or slow down |
  13
  14 ---
  15
  16 ## 📦 Features
  17
  18 - 5-lane grid with gloves (obstacles) and coins (collectibles)
  19 - 3 lives — lose one each time a glove reaches your lane
  20 - Score measured in meters traveled
  21 - Coin counter tracked separately
  22 - Crash sound effect on collision
  23 - Top-10 leaderboard saved locally via SharedPreferences (JSON)
  24 - GPS location captured on game over and reverse-geocoded to city name
  25 - Scoreboard screen with ranked list + Google Maps showing where each score was achieved
  26 - Accelerometer-based tilt control (X axis = lane change, Y axis = speed)
  27
  28 ---
  29
  30 ## 🧱 Project Structure
  31
  32 ```
  33 com.example.hw1
  34 │
  35 ├── GameManager.kt         # Game loop, 5×8 grid, spawn logic, collision detection
  36 ├── GameActivity.kt        # Main game screen, timer, button/sensor input
  37 ├── MenuActivity.kt        # Mode selection + location permission request
  38 ├── GameOverActivity.kt    # Score submission, GPS capture, reverse geocoding
  39 ├── HighScoresActivity.kt  # Hosts leaderboard + map fragments
  40 │
  41 ├── LeaderboardFragment.kt # RecyclerView ranked list
  42 ├── LeaderboardAdapter.kt  # Adapter for score rows
  43 ├── MapFragment.kt         # Google Maps with score location markers
  44 │
  45 ├── ScoreManager.kt        # Save/load top-10 scores via Gson + SharedPreferences
  46 ├── ScoreRecord.kt         # Data class: name, score, coins, lat, lng, locationName
  47 ├── GameMode.kt            # Enum: BUTTON_SLOW, BUTTON_FAST, SENSOR
  48 │
  49 ├── TiltDetector.kt        # Accelerometer listener → lane change + speed callbacks
  50 ├── TiltCallback.kt        # Interface for tilt events
  51 └── SignalManager.kt       # Toast + vibration utilities
  52 ```
  53
  54 ---
  55
  56 ## 🛠️ Tech Stack
  57
  58 - **Kotlin**
  59 - **Android SDK**
  60 - **RecyclerView** — leaderboard list
  61 - **Gson** — JSON serialization of scores
  62 - **SharedPreferences** — local score persistence
  63 - **Sensors API** — `TYPE_ACCELEROMETER` for tilt control
  64 - **Google Maps SDK** — score location display
  63 - **Sensors API** — `TYPE_ACCELEROMETER` for tilt control
  64 - **Google Maps SDK** — score location display
  65 - **FusedLocationProvider** — high-accuracy GPS on game over
  66 - **Geocoder** — reverse geocoding lat/lng to city name
  67 - **MediaPlayer** — crash sound effect
