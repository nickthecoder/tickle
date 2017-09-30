package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.action.AcceleratedXYMovement
import uk.co.nickthecoder.tickle.action.Die
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.graphics.Color

val gravity = -0.1f

class Grenade() : AbstractRole() {

    val hue: Float = 1.0f

    val action = PeriodicFactory<Actor>(10f) {

        // If we used a linear ease, instead of in-out, the sparks would be more concentrated in the middle.
        val dx = Rand.plusMinus(0.03f, Eases.easeInOut)
        val newColor = Color.createFromHSB(hue + dx, 1f, 1f)
        val transparent = Color(newColor.red, newColor.green, newColor.blue, 0f)
        val newRole = ActionRole(
                AcceleratedXYMovement(
                        // Note. By using an in-out ease, fewer intermediate speed sparks.
                        velocity = (Vector2f(dx * 20, Rand.between(2f, 12f, Eases.easeInOutExpo))),
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
            changePose(Resources.instance.pose("spark"))
            color = newColor
        }
        actor.stage?.add(newActor)


    }

    override fun begin() {
        super.begin()
        println("Grenade begin. appearance=${actor.appearance} color=${actor.color}")
    }

    override fun activated() {
        action.begin(actor)
    }

    override fun tick() {
        action.act(actor)
    }

}
