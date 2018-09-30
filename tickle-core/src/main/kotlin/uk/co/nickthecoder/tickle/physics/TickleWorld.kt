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
package uk.co.nickthecoder.tickle.physics

import org.jbox2d.callbacks.ContactListener
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.World
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game

/**
 * Part of the Physics Engine, which allows objects to collide realistically with each other.
 *
 * Tickle uses a library called JBox2d, which has a few quirks, that Tickle tries to hide from you!
 * [TickleWorld] is a wrapper around JBox2D's [World] class, which helps hide some differences
 *
 * For example, JBox2D requires a its world to be just the right size (less than 10 by 10),
 * and therefore we cannot use pixels within JBox2D.
 * Therefore all TickleWorld automatically converts between JBox2D's coordinates and Tickle's using a [scale].
 *
 * If you want to understand [velocityIterations] and [positionIterations], read the JBox2D documentation.
 * If not, leave them at their default values of 8 and 3!
 */
class TickleWorld(
        gravity: Vector2d = Vector2d(0.0, 0.0),
        val scale: Float = 100f,
        val velocityIterations: Int = 8,
        val positionIterations: Int = 3) {

    val jBox2dWorld = JBox2DWorld(pixelsToWorld(gravity), true)

    /**
     * Read the JBox2D's World.step method's documentation for more details.
     *
     * The default value is 1/30 (i.e. 30 frames per second).
     */
    var maxTimeStep = 1.0 / 30.0 // 30 frames per second

    /**
     * If the time step interval is more than maxTimeStep, should multiple steps be calculated, or just one?
     * When true, a single step is calculated, and the remaining time is ignored.
     * When false, multiple steps are calculated to fill the actual elapsed time. This will be more accurate, but
     * will take more time.
     */
    var truncate = false

    var gravity: Vector2d
        get() = worldToPixels(jBox2dWorld.gravity)
        set(v) {
            jBox2dWorld.gravity = pixelsToWorld(v)
        }

    private var previousTickSeconds: Double = 0.0

    fun pixelsToWorld(pixels: Double) = pixels.toFloat() / scale

    fun worldToPixels(world: Float) = (world * scale).toDouble()

    fun pixelsToWorld(vector2d: Vector2d) = Vec2(pixelsToWorld(vector2d.x), pixelsToWorld(vector2d.y))

    fun worldToPixels(vec2: Vec2) = Vector2d(worldToPixels(vec2.x), worldToPixels(vec2.y))

    fun pixelsToWorld(vec2: Vec2, vector2d: Vector2d) {
        vec2.x = pixelsToWorld(vector2d.x)
        vec2.y = pixelsToWorld(vector2d.y)
    }

    fun worldToPixels(vector2d: Vector2d, vec2: Vec2) {
        vector2d.x = worldToPixels(vec2.x)
        vector2d.y = worldToPixels(vec2.y)
    }

    fun createBody(bodyDef: TickleBodyDef, actor: Actor): TickleBody {
        bodyDef.position = actor.position
        bodyDef.angle = actor.direction

        val body = bodyDef.createBody(this, actor)// jBox2dWorld.createBody(bodyDef.delegated)
        bodyDef.fixtureDefs.forEach { fixtureDef ->
            val shape = fixtureDef.shapeDef.createShape(this)
            fixtureDef.shape = shape
            body.jBox2DBody.createFixture(fixtureDef)

        }
        actor.body = body
        return body
    }

    fun destroyBody(body: TickleBody) {
        jBox2dWorld.destroyBody(body.jBox2DBody)
    }

    fun tick() {
        if (previousTickSeconds == Game.instance.seconds) {
            // If the world is shared by multiple stages, then ignore the 2nd (and 3rd...) ticks
            return
        }
        previousTickSeconds = Game.instance.seconds

        // First make sure that the body is up to date. If the Actor's position or direction have been changed by game code,
        // then the Body will need to be updated before we can call "step".
        var body = jBox2dWorld.bodyList
        while (body != null) {
            val tickleBody = body.userData as TickleBody
            val actor = tickleBody.actor
            if (actor is Actor) {
                actor.ensureBodyIsUpToDate()
            }
            body = body.next
        }

        // Perform all of the JBox2D calculations
        var interval = Game.instance.tickDuration
        if (interval > maxTimeStep && truncate) {
            interval = maxTimeStep
        }
        while (interval > 0.0) {
            jBox2dWorld.step(Math.min(interval, maxTimeStep).toFloat(), velocityIterations, positionIterations)
            interval -= maxTimeStep
        }

        // Update the actor's positions and directions
        body = jBox2dWorld.bodyList
        while (body != null) {
            val tickleBody = body.userData as TickleBody
            val actor = tickleBody.actor
            actor.updateFromBody(this)
            body = body.next
        }
    }

    fun addContactListener(contactListener: ContactListener) {
        jBox2dWorld.addContactListener(contactListener)
    }

    fun removeContactListener(contactListener: ContactListener) {
        jBox2dWorld.removeContactListener(contactListener)
    }

}

class JBox2DWorld(gravity: Vec2, doSleep: Boolean) : World(gravity, doSleep) {

    fun addContactListener(contactListener: ContactListener) {
        val existingListener = m_contactManager.m_contactListener
        if (existingListener == null) {
            setContactListener(contactListener)
        } else if (existingListener is CompoundContactListener) {
            existingListener.listeners.add(contactListener)
        } else {
            val compound = CompoundContactListener()
            compound.listeners.add(existingListener)
            compound.listeners.add(contactListener)
            setContactListener(compound)
        }
    }

    fun removeContactListener(contactListener: ContactListener) {
        val existingListener = m_contactManager.m_contactListener
        if (existingListener === contactListener) {
            setContactListener(null)
        } else if (existingListener is CompoundContactListener) {
            existingListener.listeners.remove(contactListener)
            if (existingListener.listeners.isEmpty()) {
                setContactListener(null)
            }
        }
    }

}
