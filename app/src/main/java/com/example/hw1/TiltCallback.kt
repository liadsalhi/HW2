package com.example.hw1

/**
 * Callback interface used by TiltDetector to notify the game of device tilt.
 * Left/right change the player's lane; forward/back change the game speed.
 */
interface TiltCallback {
    fun tiltLeft()
    fun tiltRight()
    fun tiltSpeedUp()    // phone tilted forward
    fun tiltSpeedDown()  // phone returned upright / tilted back
}
