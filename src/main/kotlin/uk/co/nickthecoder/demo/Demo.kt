package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.AbstractProducer
import uk.co.nickthecoder.tickle.Game

class Demo : AbstractProducer() {

    init {
        instance = this
    }

    override fun begin() {
        Game.instance.window.enableVSync(1)
    }

    override fun layout() {
        if (Game.instance.window.fullScreen) {
            Game.instance.scene.layoutLetterboxed()
        } else {
            Game.instance.scene.layoutToFit()
        }
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the game.
         */
        lateinit var instance: Demo
    }
}
