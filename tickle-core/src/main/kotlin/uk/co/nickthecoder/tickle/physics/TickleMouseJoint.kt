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

import org.jbox2d.dynamics.joints.MouseJoint
import org.jbox2d.dynamics.joints.MouseJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor

/**
 * Attracts an actor towards a point (which is usually the mouse pointer's position).
 *
 * Each frame, you can update the target position. See [target].
 *
 * For example, from a Role's tick method, you could use this Kotlin snippet :
 *
 *     actor.stage?.firstView()?.mousePosition()?.let { it }
 *
 * or in Groovy :
 *
 *     mouseJoint.target( actor.stage.firstView().mousePosition() )
 *
 * Note. [actorA] and [pointA] are never used (as there's only one actor involved
 * with this type of joint).
 *
 * @param actorB The actor that is affected by this joint
 *
 * @param target The initial target point (the same as given to the [target] method).
 *
 * @param maxForce A low value will give a very elastic feel, a high value may cause actorA to be accelerated
 *        too much. The force should be in proportion to the mass of the actor's body.
 *
 * @param actorA JBox2D is weird. A mouse joint is unlike the others, in that it only affects
 *        one body. However, we cannot leave bodyA or bodyB as null (it throws)
 *        It seems that the "correct" thing to do, is to send in a dummy body as bodyA. Very annoying.
 *        This joint will have NO affect on actorA, and is here only to keep JBox2D happy!
 *        However, if you like a dangerous life, you can use the same actor for actorA and actorB.
 *        It works, but there is an assert in JBox2D's Joint class which would throw if asserts were enabled!
 */
class TickleMouseJoint(
        actorB: Actor,
        target: Vector2d,
        val maxForce: Double,
        actorA: Actor = actorB)

    : TickleJoint<MouseJoint, MouseJointDef>(actorA, actorB, Vector2d(), target) {

    init {
        actorB.body?.jBox2DBody?.isAwake = true
        create()
    }

    override fun createDef(): MouseJointDef {
        val jointDef = MouseJointDef()
        // Hmm Have I got something wrong here? A mouse joint is unlike the others, in that it only affects
        // one body. However, we cannot leave bodyA or bodyB as null (it throws), which is why I've set both
        // to the same.
        // However, Joint's constructor has an assert( bodyA != bodyB ).
        // So this only works, because asserts aren't enabled.
        // It seems that the "correct" thing to do, is to send in a dummy body as bodyA. Annoying.
        jointDef.bodyA = actorB.body!!.jBox2DBody
        jointDef.bodyB = actorB.body!!.jBox2DBody
        jointDef.maxForce = maxForce.toFloat()
        tickleWorld.pixelsToWorld(jointDef.target, pointB)
        return jointDef
    }

    fun target(point: Vector2d) {
        jBox2dJoint?.let {
            tickleWorld.pixelsToWorld(it.target, point)
        }
    }
}
