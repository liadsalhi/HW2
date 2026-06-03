package com.example.hw1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Main game screen.
 * Receives EXTRA_MODE from MenuActivity to determine controls and tick speed.
 * In SENSOR mode the speed is controlled dynamically by tilting forward/back.
 */
class GameActivity : AppCompatActivity(), TiltCallback {

    companion object {
        const val EXTRA_MODE = "game_mode"

        private const val INTERVAL_SLOW = 1000L
        private const val INTERVAL_FAST = 500L
    }

    // ── Views ─────────────────────────────────────────────────────────────────
    private lateinit var hearts: Array<ImageView>
    private lateinit var players: Array<ImageView>
    private lateinit var gloves: Array<Array<ImageView>>
    private lateinit var coins: Array<Array<ImageView>>
    private lateinit var txtCoins: TextView
    private lateinit var txtDistance: TextView
    private lateinit var controlsLayout: View
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button

    // ── Game state ────────────────────────────────────────────────────────────
    private lateinit var gameManager: GameManager
    private lateinit var gameMode: GameMode
    private var tickInterval = INTERVAL_SLOW

    private val handler = Handler(Looper.getMainLooper())
    private var gameRunning = false
    private var gameOver    = false

    // ── Sensor ────────────────────────────────────────────────────────────────
    private var tiltDetector: TiltDetector? = null

    // ─────────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameMode = GameMode.valueOf(
            intent.getStringExtra(EXTRA_MODE) ?: GameMode.BUTTON_SLOW.name
        )
        tickInterval = when (gameMode) {
            GameMode.BUTTON_SLOW -> INTERVAL_SLOW
            GameMode.BUTTON_FAST -> INTERVAL_FAST
            GameMode.SENSOR      -> INTERVAL_SLOW  // starts slow; tilt changes it
        }

        if (gameMode == GameMode.SENSOR) {
            tiltDetector = TiltDetector(this, this)
        }

        findViews()
        buildGameManager()
        setupControls()
    }

    // ── View initialization ───────────────────────────────────────────────────

    private fun findViews() {
        hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        txtCoins       = findViewById(R.id.txt_coins)
        txtDistance    = findViewById(R.id.txt_distance)
        controlsLayout = findViewById(R.id.controls_layout)
        btnLeft        = findViewById(R.id.btnLeft)
        btnRight       = findViewById(R.id.btnRight)

        players = Array(GameManager.LANES) { i ->
            val resId = resources.getIdentifier("player_$i", "id", packageName)
            findViewById(resId)
        }

        // IDs follow the pattern glove_LR / coin_LR (L = lane 0-4, R = row 0-7)
        gloves = Array(GameManager.LANES) { lane ->
            Array(GameManager.ROWS) { row ->
                val resId = resources.getIdentifier("glove_$lane$row", "id", packageName)
                findViewById(resId)
            }
        }
        coins = Array(GameManager.LANES) { lane ->
            Array(GameManager.ROWS) { row ->
                val resId = resources.getIdentifier("coin_$lane$row", "id", packageName)
                findViewById(resId)
            }
        }
    }

    private fun buildGameManager() {
        gameManager = GameManager(players, gloves, coins)
        updateHUD()
    }

    private fun setupControls() {
        if (gameMode == GameMode.SENSOR) {
            controlsLayout.visibility = View.GONE
        } else {
            controlsLayout.visibility = View.VISIBLE
            btnLeft.setOnClickListener  { gameManager.moveLeft()  }
            btnRight.setOnClickListener { gameManager.moveRight() }
        }
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    private fun startGameLoop() {
        if (gameRunning || gameOver) return
        gameRunning = true
        handler.postDelayed(gameLoopRunnable, tickInterval)
    }

    private val gameLoopRunnable = object : Runnable {
        override fun run() {
            val result = gameManager.tick()
            updateHUD()

            if (result.coinCollected) {
                SignalManager.playCoinSound()
            }

            if (result.hit) {
                SignalManager.playCrashSound(this@GameActivity)
                SignalManager.vibrate(this@GameActivity)
                showCrashNotification()

                val alive = gameManager.onHit()
                updateHUD()

                if (!alive) {
                    gameRunning = false
                    gameOver    = true
                    openGameOverScreen()
                    return
                }
            }

            if (gameRunning) {
                // tickInterval may have changed via tilt → picks up new value here
                handler.postDelayed(this, tickInterval)
            }
        }
    }

    // ── Crash notification ────────────────────────────────────────────────────

    private fun showCrashNotification() {
        SignalManager.toast(this, getString(R.string.crash_notification))
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    private fun updateHUD() {
        hearts.forEachIndexed { i, heart ->
            heart.visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
        txtCoins.text    = getString(R.string.label_coins, gameManager.coinCount)
        txtDistance.text = "${gameManager.distance} m"
    }

    // ── Game over → separate activity ─────────────────────────────────────────

    private fun openGameOverScreen() {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra(GameOverActivity.EXTRA_SCORE, gameManager.distance)
        intent.putExtra(GameOverActivity.EXTRA_COINS, gameManager.coinCount)
        startActivity(intent)
        finish()
    }

    // ── TiltCallback ──────────────────────────────────────────────────────────

    override fun tiltLeft()  { gameManager.moveLeft()  }
    override fun tiltRight() { gameManager.moveRight() }

    /** Phone tilted forward → switch to fast tick interval. */
    override fun tiltSpeedUp() {
        if (gameMode == GameMode.SENSOR) tickInterval = INTERVAL_FAST
    }

    /** Phone upright / tilted back → switch to slow tick interval. */
    override fun tiltSpeedDown() {
        if (gameMode == GameMode.SENSOR) tickInterval = INTERVAL_SLOW
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onResume() {
        super.onResume()
        startGameLoop()
        tiltDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        gameRunning = false
        handler.removeCallbacksAndMessages(null)
        tiltDetector?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        SignalManager.release()
    }
}
