/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.events.MouseButtonListener
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.physics.TickleWorld
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.scripts.ScriptManager

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
interface Director : MouseButtonListener {

    fun sceneLoaded()

    fun begin()

    fun createWorlds()

    fun activated()

    fun preTick() {
        Game.instance.scene.views.values.forEach { view ->
            view.tick()
        }
        Game.instance.scene.stages.values.forEach { stage ->
            stage.tick()
        }
    }

    fun tick()

    fun postTick() {
        Game.instance.scene.stages.values.forEach { stage ->
            stage.world?.tick()
        }
    }

    fun end()

    fun onKey(event: KeyEvent)

    fun message(message: String)

    companion object {
        fun createDirector(directorString: String): Director {
            try {
                val klass = ScriptManager.classForName(directorString)
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

    /**
     * When true, prevent all stage, views and roles from receiving tick events.
     * You can override this behaviour by changing the implementation of [preTick] and [postTick]
     */
    var paused: Boolean = false

    /**
     * The default behaviour is to create a single shared world attached to every Stage.
     * It is quite common to override this method, and create a single world on just one stage.
     */
    override fun createWorlds() {
        if (Resources.instance.gameInfo.physicsEngine) {
            val pi = Resources.instance.gameInfo.physicsInfo
            val world: TickleWorld = TickleWorld(pi.gravity, pi.scale.toFloat(), velocityIterations = pi.velocityIterations, positionIterations = pi.positionIterations)
            Game.instance.scene.stages.values.forEach { stage ->
                stage.world = world
            }
        }
    }

    /**
     * The default implementation does nothing.
     */
    override open fun sceneLoaded() {}

    /**
     * The default implementation does nothing.
     */
    override open fun begin() {}

    /**
     * The default implementation does nothing.
     */
    override open fun activated() {}

    /**
     * The default implementation does nothing.
     */
    override open fun end() {}

    /**
     * The default implementation does nothing.
     */
    override open fun onKey(event: KeyEvent) {}

    /**
     * The default implementation does nothing.
     */
    override open fun onMouseButton(event: MouseEvent) {}

    /**
     * If [paused] is true, does nothing, otherwise it calls tick on all views, then calls tick on all stages.
     */
    override open fun preTick() {
        if (!paused) {
            super.preTick()
        }
    }

    /**
     * The default implementation does nothing.
     */
    override open fun tick() {}

    /**
     * If [paused] is true, does nothing, otherwise it calls tick on each stage's TickleWorld (the physics engine).
     */
    override open fun postTick() {
        if (!paused) {
            super.postTick()
        }
    }

    /**
     * The default implementation does nothing.
     */
    override fun message(message: String) {}
}

class NoDirector : AbstractDirector()
