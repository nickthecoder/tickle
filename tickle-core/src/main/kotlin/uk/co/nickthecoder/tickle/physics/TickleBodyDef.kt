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

import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Copyable

/**
 * Holds details of a Costume's body. This differs from the usual [BodyDef], by defining the FixturesDef
 * rather than raw Shape objects.
 * Also note that the units used here are in Tickle's (unscaled) coordinate system (which is generally pixels),
 * whereas the Body objects that get created via this TickleBodyDef use JBox2D's coordinate system, which is
 * (apparently), not suitable for units of pixels (the docs say objects must be up to 10 units in size).
 * Therefore, when converting this TickleBodyDef to an actual Body, the [TickleWorld]'s scale is used to convert
 * the units.
 */
class TickleBodyDef() : Copyable<TickleBodyDef> {

    val fixtureDefs = mutableListOf<TickleFixtureDef>()

    override fun copy(): TickleBodyDef {
        val copy = TickleBodyDef()
        copy.allowSleep = allowSleep
        copy.angle = angle
        copy.angularDamping = angularDamping
        copy.bullet = bullet
        copy.fixedRotation = fixedRotation
        copy.linearDamping = linearDamping
        copy.position.set(position)
        copy.type = type

        fixtureDefs.forEach { fixtureDef ->
            copy.fixtureDefs.add(fixtureDef.copy())
        }

        return copy
    }

    var allowSleep = true

    var angle = Angle.radians(0.0)

    var angularDamping = 0.0

    var bullet = false

    var fixedRotation = false

    var linearDamping = 0.0

    var position = Vector2d()

    var type = BodyType.STATIC

    // TODO Return a TickleBody, when that is written.
    fun createBody(tickleWorld: TickleWorld, actor: Actor): TickleBody {
        val bodyDef = BodyDef()
        bodyDef.allowSleep = allowSleep
        bodyDef.angle = angle.radians.toFloat()
        bodyDef.angularDamping = angularDamping.toFloat()
        bodyDef.bullet = bullet
        bodyDef.fixedRotation = fixedRotation
        bodyDef.linearDamping = linearDamping.toFloat()
        bodyDef.position = tickleWorld.pixelsToWorld(position)
        bodyDef.type = type

        return TickleBody(tickleWorld.jBox2dWorld.createBody(bodyDef), tickleWorld, actor)
    }

}
