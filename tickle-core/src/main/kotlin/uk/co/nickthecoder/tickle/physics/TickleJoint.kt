package uk.co.nickthecoder.tickle.physics

import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.joints.Joint
import org.jbox2d.dynamics.joints.JointDef
import org.joml.Vector2d
import uk.co.nickthecoder.tickle.Actor

/**
 * JBox2D uses [Joint] and [JointDef], and it seems that you cannot change some aspects of a [Joint],
 * without deleting the joint, and adding a new one.
 *
 * As Tickle is trying to be easy to use (rather than maximally efficient), a TickleJoint CAN be
 * dynamically altered, without extra hassle (it deletes the [Joint], and creates a new one for you).
 * Unless you change joints extremely often, this won't noticeably affect performance.
 */
abstract class TickleJoint<J : Joint, D : JointDef>(
        val actorA: Actor,
        val actorB: Actor,
        pointA: Vector2d,
        pointB: Vector2d) {

    protected var jBox2dJoint: J? = null

    val tickleWorld: TickleWorld = actorA.body?.tickleWorld ?: throw IllegalArgumentException("actorA does not have a body. Check the 'Physics' tab of its costume.")

    var pointA = pointA
        set(v) {
            field = v
            replace()
        }

    var pointB = pointB
        set(v) {
            field = v
            replace()
        }

    init {
        val bodyB = actorB.body ?: throw IllegalArgumentException("actorB does not have a body. Check the 'Physics' tab of its costume.")
        if (actorA.body?.tickleWorld != actorB.body?.tickleWorld) throw IllegalArgumentException("actorA's world is not the same as actorB's")
    }

    /**
     * This is a more efficient version of [getAnchorA] (a new Vector2d isn't created)
     */
    fun anchorA(anchorA: Vector2d) {
        jBox2dJoint?.getAnchorA(tmpVec2)
        tickleWorld.worldToPixels(anchorA, tmpVec2)
    }

    /**
     * This is a more efficient version of [getAnchorB] (a new Vector2d isn't created)
     */
    fun anchorB(out: Vector2d) {
        jBox2dJoint?.getAnchorB(tmpVec2)
        tickleWorld.worldToPixels(out, tmpVec2)
    }

    /**
     * Gets the position of the joint's anchor position.
     * For [TicklePinJoint], this will be the same as [getAnchorB]
     */
    fun getAnchorA(): Vector2d {
        val out = Vector2d()
        anchorA(out)
        return out
    }

    /**
     * Gets the position of the joint's anchor position.
     * For [TicklePinJoint], this will be the same as [getAnchorA]
     */
    fun getAnchorB(): Vector2d {
        val out = Vector2d()
        anchorB(out)
        return out
    }

    protected abstract fun createDef(): D

    protected fun create() {
        @Suppress("UNCHECKED_CAST")
        jBox2dJoint = tickleWorld.jBox2dWorld.createJoint(createDef()) as J
        jBox2dJoint?.userData = this
    }

    protected fun replace() {
        destroy()
        create()
    }

    fun destroy() {
        jBox2dJoint?.let { tickleWorld.jBox2dWorld.destroyJoint(it) }
        jBox2dJoint = null
    }
}

private val tmpVec2 = Vec2()
