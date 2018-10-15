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

import org.jbox2d.dynamics.joints.RevoluteJoint
import org.jbox2d.dynamics.joints.RevoluteJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Join two actors together, as if you had stuck a pin through both of them.
 * Both actors can rotate about the pin point. The joint will not stop them moving in other ways
 * (i.e. the pin, does NOT pin them to a certain point in the scene).
 *
 * In JBox2D this is known as a "RevoluteJoint"
 *
 * NOTE. It is up to you to destroy the joint (for example, if actorA or actorB dies before the scene is over).
 *
 * @param pointA The position of the pin, relative to [actorA].
 *        This is often (0,0), if you want the pin at the "middle" of actorA.
 *        Note, you do NOT have to account for the actor's rotation (if it is rotated),
 *        but you DO have to take account of its scale (if it has been scaled).
 *
 * @param pointB The position of the pin, relative to [actorB].
 *        This is often (0,0), if you want the pin at the "middle" of actorB.
 *        Note, you do NOT have to account for the actor's rotation (if it is rotated),
 *        but you DO have to take account of its scale (if it has been scaled).
 */
class TicklePinJoint(
        actorA: Actor,
        actorB: Actor,
        pointA: Vector2d = Vector2d(),
        pointB: Vector2d = Vector2d())

    : TickleJoint<RevoluteJoint, RevoluteJointDef>(actorA, actorB, pointA, pointB) {

    private var fromAngle: Angle? = null
    private var toAngle: Angle? = null
    private var limitedRotation = false


    var collideConnected = false
        set(v) {
            field = v
            replace()
        }

    init {
        create()
    }

    override fun createDef(): RevoluteJointDef {
        val jointDef = RevoluteJointDef()
        jointDef.bodyA = actorA.body!!.jBox2DBody
        jointDef.bodyB = actorB.body!!.jBox2DBody
        jointDef.localAnchorA = tickleWorld.pixelsToWorld(pointA)
        jointDef.localAnchorB = tickleWorld.pixelsToWorld(pointB)

        if (isRotationLimited()) {
            jointDef.enableLimit = true
            jointDef.lowerAngle = fromAngle!!.radians.toFloat()
            jointDef.upperAngle = toAngle!!.radians.toFloat()
        }
        jointDef.collideConnected = collideConnected
        return jointDef
    }

    fun limitRotation(from: Angle, to: Angle) {
        limitedRotation = true
        fromAngle = from
        toAngle = to
        replace()
    }

    /**
     * The opposite of [limitedRotation].
     */
    fun freeRotation() {
        limitedRotation = false
        jBox2dJoint?.enableLimit(false)
    }

    fun isRotationLimited() = limitedRotation

    fun rotationLimits() = Pair(fromAngle, toAngle)

}
