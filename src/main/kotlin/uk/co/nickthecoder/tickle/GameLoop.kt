package uk.co.nickthecoder.tickle

interface GameLoop {
    val tickCount: Long
    fun tick()
    fun resetStats()
    fun actualFPS(): Double
}
