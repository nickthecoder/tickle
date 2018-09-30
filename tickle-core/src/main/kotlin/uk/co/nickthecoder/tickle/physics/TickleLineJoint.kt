package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.joints.LineJoint
import org.jbox2d.dynamics.joints.LineJointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.util.Angle

/**
 * Join two actors together, as if there were an extendable rod between them.
 * By default, this rod can grow infinitely long, but can be limited
 * using [limitTranslation].
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
class TickleLineJoint(actorA: Actor, actorB: Actor, pointA: Vector2d, pointB: Vector2d)
    : TickleJoint<LineJoint, LineJointDef>(actorA, actorB, pointA, pointB) {

    var referenceAngle: Angle = Angle.radians(0.0)
        set(v) {
            field = v
            replace()
        }

    override fun createDef(): LineJointDef {
        val jointDef = LineJointDef()
        jointDef.bodyA = actorA.body!!.jBox2DBody
        jointDef.bodyB = actorB.body!!.jBox2DBody
        tickleWorld.pixelsToWorld(jointDef.localAnchorA, pointA)
        tickleWorld.pixelsToWorld(jointDef.localAnchorB, pointB)

        return jointDef
    }

    /**
     * Prevents the extendable rod from becoming too short, or too long.
     * The opposite is [freeTranslation].
     */
    fun limitTranslation(lower: Double, upper: Double) {
        jBox2dJoint?.setLimits(tickleWorld.pixelsToWorld(lower), tickleWorld.pixelsToWorld(upper))
    }

    /**
     * The opposite of [limitTranslation]
     */
    fun freeTranslation() {
        jBox2dJoint?.EnableLimit(false)
    }

    fun getLimits(): Pair<Double, Double>? {
        jBox2dJoint?.let {
            if (it.isLimitEnabled) {
                return Pair(tickleWorld.worldToPixels(it.lowerLimit), tickleWorld.worldToPixels(it.upperLimit))
            } else {
                return null
            }
        }
        return null
    }

}
