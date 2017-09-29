package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Color

class Demo : Producer {

    init {
        instance = this
    }

    override fun begin() {
        println("Demo being")
        Game.instance.window.enableVSync(1)
    }

    override fun startScene(sceneName: String) {
        println("Demo startScene")
        // TODO Remove when scene loading is implemented
        Game.instance.director = Play()
        Game.instance.renderer.clearColor(Color.BLACK)
        Game.instance.director.begin()
    }

    companion object {
        /**
         * A convenience, so that game scripts can easily get access to the game.
         */
        lateinit var instance: Demo
    }
}
