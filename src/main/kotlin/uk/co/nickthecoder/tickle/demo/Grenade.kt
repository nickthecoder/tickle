package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.action.AcceleratedXYMovement
import uk.co.nickthecoder.tickle.action.Die
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.graphics.Color

val gravity = -0.1f

class Grenade(val hue: Float) : AbstractRole() {

    val action = PeriodicFactory(10f) {

        val dx = Rand.plusMinus(0.03f)
        val newColor = Color.createFromHSB(hue + dx, 1f, 1f)
        val transparent = Color(newColor.red, newColor.green, newColor.blue, 0f)
        val newRole = ActionRole(
                AcceleratedXYMovement(
                        velocity = (Vector2f(dx * 20, Rand.between(10f, 5f))),
                        drag = 0.01f,
                        acceleration = Vector2f(0f, gravity)
                ).and(
                        Fade(3f, transparent)
                                .then(Die()
                                )
                )
        )
        val newActor = Actor(newRole)

        with(newActor) {
            x = actor.x
            y = actor.y
            z = actor.z - 1
            changePose(Resources.instance.sparkPose)
            color = newColor
        }
        actor.stage?.add(newActor)


    }

    override fun activated() {
        action.begin(actor)
    }

    override fun tick() {
        action.act(actor)
    }

}
