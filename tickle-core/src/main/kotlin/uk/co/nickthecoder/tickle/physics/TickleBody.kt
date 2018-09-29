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
