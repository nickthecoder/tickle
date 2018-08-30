package uk.co.nickthecoder.tickle.loop

import uk.co.nickthecoder.tickle.Game

abstract class AbstractGameLoop(val game: Game) : GameLoop {

    private var startNanos = System.nanoTime()

    override var tickCount = 0L

    override fun resetStats() {
        startNanos = System.nanoTime()
        tickCount = 0
    }

    override fun actualFPS(): Double {
        return (tickCount.toDouble() * 1_000_000_000 / (System.nanoTime() - startNanos))
    }
}
