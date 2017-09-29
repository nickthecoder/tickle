package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Color

class Demo : Producer {

    init {
        instance = this
    }

    override fun begin() {
        Game.instance.renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        Game.instance.window.enableVSync(1)

        // The following code will be replaced by loading a scene from a json file when that is written.

        Game.instance.director = Play()
        Game.instance.director.begin()
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the game.
         */
        lateinit var instance: Demo
    }
}
