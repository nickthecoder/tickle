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
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game

class TickleWorld(
        gravity: Vector2d = Vector2d(0.0, 0.0),
        val scale: Float = 100f,
        val velocityIterations: Int = 8,
        val positionIterations: Int = 3)

    : World(Vec2(gravity.x.toFloat() / scale, gravity.y.toFloat() / scale), true) {

    val maxTimeStep = 1.0 / 30.0 // 30 frames per second

    /** If the time step interval is more than maxTimeStep, should multiple steps be calculated, or just one?
     * When true, a single step is calculated, and the remaining time is ignored.
     * When false, multiple steps are calculated to fill the actual elapsed time. This will be more acurate, but
     * will take more time.
     */
    val truncate = false

    private val tempVec = Vec2()

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

    fun createBody(bodyDef: TickleBodyDef, actor: Actor): Body {
        bodyDef.position = pixelsToWorld(actor.position)
        bodyDef.angle = actor.direction.radians.toFloat()

        val body = createBody(bodyDef)
        bodyDef.fixtureDefs.forEach { fixtureDef ->
            val shape = fixtureDef.shapeDef.createShape(this)
            fixtureDef.shape = shape
            body.createFixture(fixtureDef)

        }
        actor.body = body
        body.userData = actor
        return body
    }

    fun tick() {
        if (previousTickSeconds == Game.instance.seconds) {
            // If the world is shared by multiple stages, then ignore the 2nd (and 3rd...) ticks
            return
        }
        previousTickSeconds = Game.instance.seconds

        // First make sure that the body is up to date. If the Actor's position or direction have been changed by game code,
        // then the Body will need to be updated before we can call "step".
        var body = bodyList
        while (body != null) {
            val actor = body.userData
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
            step(Math.min(interval, maxTimeStep).toFloat(), velocityIterations, positionIterations)
            interval -= maxTimeStep
        }

        // Update the actor's positions and directions
        body = bodyList
        while (body != null) {
            val actor = body.userData
            if (actor is Actor) {
                actor.updateFromBody(this)
            }
            body = body.next
        }
    }

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
