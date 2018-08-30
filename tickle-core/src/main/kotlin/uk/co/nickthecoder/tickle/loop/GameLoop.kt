package uk.co.nickthecoder.tickle.loop

interface GameLoop {
    val tickCount: Long
    fun tick()
    fun resetStats()
    fun actualFPS(): Double
}
