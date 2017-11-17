package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.events.MouseButtonHandler
import uk.co.nickthecoder.tickle.events.MouseEvent

/**
 * Looks after a single Scene. A game will typically have at least two Directors, one to handle the menu or splash
 * screen, and another for playing the game. They are typically called "Menu" and "Play".
 *
 * A typical game has many "levels", and a Director is created when a new level is loaded, and is delete when the
 * level ends. Therefore they cannot hold information that is carried over from one level to the next (such as
 * lives remaining). Such information must be held on Producer instead.
 *
 * Typical uses of a Directory :
 *
 * Decide when the level has been completed (or failed), and move to the next level, or return to the main menu.
 * Play music
 * Act as a single point of communication between the Actors' Roles
 * Holds "tag" data for each Role in the scene (see tagManager)
 *
 * There are many more things that Director can do!
 */
interface Director : MouseButtonHandler {

    fun sceneLoaded()

    fun begin()

    fun activated()

    fun preTick()

    fun tick() {
        Game.instance.scene.tick()
    }

    fun postTick()

    fun end()

    fun onKey(event: KeyEvent)

    companion object {
        fun createDirector(directorString: String): Director {
            try {
                val klass = Class.forName(directorString)
                val newDirector = klass.newInstance()
                if (newDirector is Director) {
                    return newDirector
                } else {
                    System.err.println("'$directorString' is not a type of Director")
                }
            } catch (e: Exception) {
                System.err.println("Failed to create a Director from : '$directorString'")
            }
            return NoDirector()
        }
    }
}

abstract class AbstractDirector : Director {

    override fun sceneLoaded() {}

    override fun begin() {}

    override fun activated() {}

    override fun end() {}

    override fun onKey(event: KeyEvent) {}

    override fun onMouseButton(event: MouseEvent) {}

    override fun postTick() {}

    override fun preTick() {}
}

class NoDirector : AbstractDirector()
