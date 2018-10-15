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

import org.jbox2d.dynamics.Body
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor

class TickleBody(
        val jBox2DBody: Body,
        val tickleWorld: TickleWorld,
        val actor: Actor) {

    init {
        jBox2DBody.userData = this
    }

    val mass: Double
        get() = jBox2DBody.mass.toDouble()

    private val pLinearVelocity = Vector2d()
    val linearVelocity: Vector2d
        get() {
            tickleWorld.worldToPixels(pLinearVelocity, jBox2DBody.linearVelocity)
            return pLinearVelocity
        }

    var angularVelocity: Double
        get() = tickleWorld.worldToPixels(jBox2DBody.angularVelocity)
        set(v) {
            jBox2DBody.angularVelocity = v.toFloat()
        }

    /**
     * Explicitly set the velocity.
     * This is most often called when "shooting" something, giving it an initial kick.
     * From then on the physics engine takes care of the velocity.
     */
    fun setLinearVelocity(velocity: Vector2d) {
        tickleWorld.pixelsToWorld(jBox2DBody.linearVelocity, velocity)
    }
}

fun Body.tickleBody() = (userData as TickleBody)

fun Body.actor() = tickleBody().actor
