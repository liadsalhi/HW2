package com.example.hw2

import android.view.View
import android.widget.ImageView

/**
 * All game logic: 5-lane road, glove obstacles, coins, lives, odometer.
 * The UI layer (GameActivity) passes ImageView arrays; GameManager only
 * sets their visibility – it never touches layout parameters.
 */
class GameManager(
    private val players: Array<ImageView>,          // players[0..4] – one per lane
    private val gloves: Array<Array<ImageView>>,    // gloves[lane][row], 5×8
    private val coins: Array<Array<ImageView>>      // coins[lane][row], 5×8
) {

    companion object {
        const val LANES = 5
        const val ROWS = 8
        const val MAX_LIVES = 3
        private const val GLOVE_SPAWN_EVERY = 3   // ticks between glove spawns
        private const val COIN_SPAWN_EVERY = 7    // ticks between coin spawns
    }

    var currentLane: Int = LANES / 2   // start in center lane
        private set

    var lives: Int = MAX_LIVES
        private set

    var coinCount: Int = 0
        private set

    var distance: Int = 0
        private set

    private var frameCount = 0

    /** Returned by tick() so GameActivity knows what happened this frame. */
    data class TickResult(val hit: Boolean, val coinCollected: Boolean)

    init {
        showOnlyCurrentPlayer()
        clearBoard()
    }

    // ── Controls ─────────────────────────────────────────────────────────────

    fun moveLeft()  = moveTo(currentLane - 1)
    fun moveRight() = moveTo(currentLane + 1)

    private fun moveTo(lane: Int) {
        if (lane in 0 until LANES) {
            currentLane = lane
            showOnlyCurrentPlayer()
        }
    }

    // ── Hit / reset ───────────────────────────────────────────────────────────

    /** Called when a collision is detected. Returns true if player is still alive. */
    fun onHit(): Boolean {
        lives--
        return lives > 0
    }

    /** Resets everything for a new game. */
    fun resetGame() {
        lives = MAX_LIVES
        coinCount = 0
        distance = 0
        frameCount = 0
        currentLane = LANES / 2
        clearBoard()
        showOnlyCurrentPlayer()
    }

    // ── Game loop ─────────────────────────────────────────────────────────────

    /**
     * Advance the game by one tick.
     * 1. Shift all gloves and coins one row down.
     * 2. Maybe spawn a new glove at the top.
     * 3. Maybe spawn a new coin at the top.
     * 4. Check if the bottom row of the player's lane has a glove (hit) or coin.
     * 5. Increment distance counter.
     */
    fun tick(): TickResult {
        frameCount++
        distance++

        shiftColumn(gloves)
        shiftColumn(coins)

        // Spawn glove
        if (frameCount % GLOVE_SPAWN_EVERY == 0) {
            spawnAtTop(gloves, exclude = coins)
        }

        // Spawn coin (never on the same top cell as a glove)
        if (frameCount % COIN_SPAWN_EVERY == 0) {
            spawnAtTop(coins, exclude = gloves)
        }

        // Check bottom row in player's lane
        val bottomGlove = gloves[currentLane][ROWS - 1].visibility == View.VISIBLE
        val bottomCoin  = coins[currentLane][ROWS - 1].visibility == View.VISIBLE

        if (bottomGlove) gloves[currentLane][ROWS - 1].visibility = View.INVISIBLE
        if (bottomCoin)  {
            coins[currentLane][ROWS - 1].visibility = View.INVISIBLE
            coinCount++
        }

        return TickResult(hit = bottomGlove, coinCollected = bottomCoin)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Move every item in [grid] one row downward (row 7 disappears). */
    private fun shiftColumn(grid: Array<Array<ImageView>>) {
        for (lane in 0 until LANES) {
            for (row in ROWS - 1 downTo 1) {
                grid[lane][row].visibility = grid[lane][row - 1].visibility
            }
            grid[lane][0].visibility = View.INVISIBLE
        }
    }

    /**
     * Pick a random lane whose top cell (row 0) is free in both [grid]
     * and [exclude], then make that cell VISIBLE in [grid].
     */
    private fun spawnAtTop(grid: Array<Array<ImageView>>, exclude: Array<Array<ImageView>>) {
        val freeLanes = (0 until LANES).filter {
            grid[it][0].visibility == View.INVISIBLE &&
            exclude[it][0].visibility == View.INVISIBLE
        }
        if (freeLanes.isNotEmpty()) {
            grid[freeLanes.random()][0].visibility = View.VISIBLE
        }
    }

    private fun showOnlyCurrentPlayer() {
        players.forEachIndexed { i, player ->
            player.visibility = if (i == currentLane) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun clearBoard() {
        for (lane in 0 until LANES) {
            for (row in 0 until ROWS) {
                gloves[lane][row].visibility = View.INVISIBLE
                coins[lane][row].visibility  = View.INVISIBLE
            }
        }
    }
}