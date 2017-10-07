package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.AbstractRole
import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Resources
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.action.animation.Eases
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.movement.Accelerate
import uk.co.nickthecoder.tickle.action.movement.Drag
import uk.co.nickthecoder.tickle.action.movement.Move
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Rand

val gravity = Vector2f(0f, -0.1f)

class Grenade() : AbstractRole() {

    @Attribute
    var hue: Float = 1.0f

    val action = PeriodicFactory<Actor>(10f) {

        // If we used a linear ease, instead of in-out, the sparks would be more concentrated in the middle.
        val dx = Rand.plusMinus(0.03f, Eases.easeInOut)
        val newColor = Color.createFromHSB(hue + dx, 1f, 1f)
        val transparent = Color(newColor.red, newColor.green, newColor.blue, 0f)
        val newRole = ActionRole()

        val newActor = Actor(newRole)

        with(newActor) {
            x = actor.x
            y = actor.y
            z = actor.z - 1
            changePose(Resources.instance.pose("spark"))
            color = newColor
        }
        actor.stage?.add(newActor)

        val velocity = Vector2f(dx * 20, Rand.between(2f, 12f, Eases.easeInOutExpo))
        val movement = Move(newActor.position, velocity).and(Accelerate(velocity, gravity)).and(Drag(velocity, 0.01f))
        newRole.action = movement.and(Fade(newActor, 3f, transparent).then(Kill(newActor)))

    }

    override fun activated() {
        action.begin()
    }

    override fun tick() {
        action.act()
    }

}
