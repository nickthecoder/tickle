package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.KeyEvent
import java.io.File

/**
 * Looks at the big picture, and shouldn't be involved in the minor details.
 * Your Producer class (if you have one) should named after your game. e.g. "SpaceInvaders"
 * Many games don't need a Producer, and therefore use the class NoProducer.
 *
 * Example responsibilities of a Producer :
 *
 * Perform actions when the game first starts and when it ends.
 * Add additional auto-generated resources to the Resources class at the beginning of the game.
 * Take a snapshot of the screen when a particular key is pressed.
 * To hold information carried forward from one level to the next, such as score and lives remaining.
 *
 * See [Director]
 */
interface Producer {

    fun begin()

    fun startScene(sceneFile: File) {
        Game.instance.endScene()
        val scene = Game.instance.loadScene(sceneFile)
        Game.instance.startScene(scene)
    }

    fun sceneBegin()

    fun sceneActivated()

    fun preTick()

    fun postTick()

    fun sceneEnd()

    fun end()

    fun onKeyEvent(event: KeyEvent)
}

abstract class AbstractProducer : Producer {

    override fun begin() {}

    override fun sceneBegin() {}

    override fun sceneActivated() {}

    override fun postTick() {}

    override fun preTick() {}

    override fun sceneEnd() {}

    override fun end() {}

    override fun onKeyEvent(event: KeyEvent) {}
}

class NoProducer : AbstractProducer()
