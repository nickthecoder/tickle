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
 */
class TickleMouseJoint(
        actorB: Actor,
        target: Vector2d,
        val maxForce: Double)

    : TickleJoint<MouseJoint, MouseJointDef>(actorB, actorB, Vector2d(), target) {

    init {
        create()
    }

    override fun createDef(): MouseJointDef {
        val jointDef = MouseJointDef()
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
