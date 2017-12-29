package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.events.MouseButtonListener
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.events.ResizeEvent
import uk.co.nickthecoder.tickle.resources.Resources
import java.util.prefs.Preferences

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
interface Producer : MouseButtonListener {

    fun begin()

    fun startScene(scenePath: String) {
        Game.instance.endScene()
        Game.instance.startScene(scenePath)
    }

    fun sceneLoaded()

    fun sceneBegin()

    fun sceneActivated()

    fun preTick()

    fun postTick()

    fun sceneEnd()

    fun end()

    fun onKey(event: KeyEvent)

    fun onResize(event: ResizeEvent)

    fun message(message: String)

    /**
     * Gets the preferences node, for persisting data, such as high-scores, levels unlocked etc.
     * The default node uses Tickle's package name and the id of your game (as defined in [GameInfo]).
     */
    fun preferencesRoot(): Preferences {
        return Preferences.userNodeForPackage(Game::class.java).node(Resources.instance.gameInfo.id)
    }

}

abstract class AbstractProducer : Producer {

    override fun sceneLoaded() {}

    override fun begin() {}

    override fun sceneBegin() {}

    override fun sceneActivated() {}

    override fun postTick() {}

    override fun preTick() {}

    override fun sceneEnd() {}

    override fun end() {}

    override fun onKey(event: KeyEvent) {}

    override fun onMouseButton(event: MouseEvent) {}

    /**
     * The default implementation resizes all views according to the definitions in the Layout
     * (which is defined in the Editor).
     */
    override fun onResize(event: ResizeEvent) {
        Game.instance.scene.layout(event.width, event.height)
    }

    override fun message(message: String) {}

}

class NoProducer : AbstractProducer()
