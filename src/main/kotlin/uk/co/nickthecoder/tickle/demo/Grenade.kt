package uk.co.nickthecoder.tickle.demo

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.movement.polar.DragPolar
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Polar2f
import uk.co.nickthecoder.tickle.util.Rand

class Grenade() : AbstractRole() {

    @Attribute(AttributeType.POLAR, scale = 10f)
    var minVelocity = Polar2f(Angle.degrees(-20.0), 2f)

    @Attribute(AttributeType.POLAR, scale = 20f)
    var maxVelocity = Polar2f(Angle.degrees(20.0), 12f)

    @Attribute(AttributeType.RELATIVE_POSITION)
    var exit = Vector2f(0f, 50f)

    @Attribute
    var hue: Float = 1.0f

    val action = PeriodicFactory<Actor>(10f) {

        val randomHue = Rand.plusMinus(0.06f)
        val newColor = Color.createFromHSB(hue + randomHue, 1f, 1f)
        val transparent = Color(newColor.red, newColor.green, newColor.blue, 0f)
        val newRole = ActionRole()

        val newActor = Actor(newRole)

        with(newActor) {
            x = actor.x + exit.x
            y = actor.y + exit.y
            z = actor.z - 1
            changePose(Resources.instance.pose("spark"))
            color = newColor
        }
        actor.stage?.add(newActor)

        val velocity = Rand.between(minVelocity, maxVelocity)
        val movement = MovePolar(newActor.position, velocity).and(DragPolar(velocity, 0.015f))
        newRole.action = movement.and(Fade(newActor, 3f, transparent).then(Kill(newActor)))

    }

    override fun activated() {
        action.begin()
    }

    override fun tick() {
        action.act()
    }

}
