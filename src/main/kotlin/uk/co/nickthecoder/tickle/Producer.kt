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

    fun layout()

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
     * The default implementation calls [Scene.layoutToFit] and [Scene.adjustActors], so that the views fit into the
     * available space, all actors are re-positioned according to their alignment.
     * e.g. if an Actor is right aligned and the window is expanded, then the actor will move to the right.
     */
    override fun onResize(event: ResizeEvent) {
        Game.instance.scene.layoutToFit()
        Game.instance.scene.adjustActors(event.width - event.oldWidth.toDouble(), event.height - event.oldHeight.toDouble())
    }

    /**
     * If [GameInfo.fullScreen] == true, and [GameInfo.resizable] == false, then uses [Scene.layoutWithMargins].
     * This is needed, because the window may not be the same size as that in GameInfo (when the monitor does not support
     * the GameInfo size). In this case, the next-highest screen size will have been picked, and we center the views and
     * add margins to fill the remaining space.
     * If the monitor only supports smaller sizes, then the views will end up being cropped.
     * Cropping is the "safest" option, because when [GameInfo.resizable] == false, it is likely that the game hasn't been
     * designed to support resizing.
     * TODO If/when Tickle supports automatic scaling, then this default behaviour will change, so that cropping never happens.
     *
     * In all other cases, it calls [Scene.layoutToFit].
     */
    override fun layout() {
        val gameInfo = Resources.instance.gameInfo
        if (gameInfo.fullScreen && gameInfo.resizable == false) {
            Game.instance.scene.layoutWithMargins()
        } else {
            Game.instance.scene.layoutToFit()
        }
    }

    override fun message(message: String) {}

}

class NoProducer : AbstractProducer()
