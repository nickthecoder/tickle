package uk.co.nickthecoder.tickle.loop

import uk.co.nickthecoder.tickle.Game

/**
 * Runs as quickly as possible, Will be capped to the screen's refresh rate if using v-sync.
 */
class FullSpeedGameLoop(game: Game) : AbstractGameLoop(game) {

    override fun tick() {

        tickCount++
        game.tick()
        game.window.swap()

    }

}
