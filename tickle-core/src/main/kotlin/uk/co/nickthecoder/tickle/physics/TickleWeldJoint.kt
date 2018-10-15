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

import org.jbox2d.dynamics.joints.WeldJoint
import org.jbox2d.dynamics.joints.WeldJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Join two actors together, as if welded together.
 *
 * NOTE. It is up to you to destroy the joint (for example, if actorA or actorB dies before the scene is over).
 *
 * @param pointA The position of the pin, relative to [actorA].
 *        This is (0,0), if you want them welded at the "middle" of actorA.
 *        Note, you do NOT have to account for the actor's rotation (if it is rotated),
 *        but you DO have to take account of its scale (if it has been scaled).
 *
 * @param pointB The position of the pin, relative to [actorB].
 *        This is (0,0), if you want them welded at the "middle" of actorB.
 *        Note, you do NOT have to account for the actor's rotation (if it is rotated),
 *        but you DO have to take account of its scale (if it has been scaled).
 */
class TickleWeldJoint(actorA: Actor, actorB: Actor, pointA: Vector2d, pointB: Vector2d)
    : TickleJoint<WeldJoint, WeldJointDef>(actorA, actorB, pointA, pointB) {

    var referenceAngle: Angle = Angle.radians(0.0)
        set(v) {
            field = v
            replace()
        }

    override fun createDef(): WeldJointDef {
        val jointDef = WeldJointDef()
        jointDef.bodyA = actorA.body!!.jBox2DBody
        jointDef.bodyB = actorB.body!!.jBox2DBody
        tickleWorld.pixelsToWorld(jointDef.localAnchorA, pointA)
        tickleWorld.pixelsToWorld(jointDef.localAnchorB, pointB)

        jointDef.referenceAngle = referenceAngle.radians.toFloat()

        return jointDef
    }

}
