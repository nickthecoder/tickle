package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.graphics.Window

class Demo(
        window: Window,
        resources: Resources) : Game(window, resources) {

    override fun preInitialise() {
        instance = this
    }

    override fun postInitialise() {
        renderer.clearColor(Color(1.0f, 1.0f, 1.0f, 1.0f))

        window.enableVSync(1)
        window.keyboardEvents { onKeyEvent(it) }

        // The following code will be replaced by loading a scene from a json file when that is written.

        director = Play()

        director.begin()
    }

    override fun tick() {
        director.preTick()
        director.postTick()

    }

    override fun postCleanup() {
        println("Demo ended cleanly")
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the game.
         */
        lateinit var instance: Demo
    }
}
