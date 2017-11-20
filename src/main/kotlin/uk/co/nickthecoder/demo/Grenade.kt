package uk.co.nickthecoder.demo

import org.joml.Vector2d
import uk.co.nickthecoder.tickle.*
import uk.co.nickthecoder.tickle.action.Kill
import uk.co.nickthecoder.tickle.action.PeriodicFactory
import uk.co.nickthecoder.tickle.action.animation.Fade
import uk.co.nickthecoder.tickle.action.movement.polar.DragPolar
import uk.co.nickthecoder.tickle.action.movement.polar.MovePolar
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.resources.Resources
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Attribute
import uk.co.nickthecoder.tickle.util.Polar2d
import uk.co.nickthecoder.tickle.util.Rand

class Grenade() : AbstractRole() {

    @Attribute(AttributeType.POLAR, scale = 10.0)
    var minVelocity = Polar2d(Angle.degrees(-20.0), 2.0)

    @Attribute(AttributeType.POLAR, scale = 20.0)
    var maxVelocity = Polar2d(Angle.degrees(20.0), 12.0)

    @Attribute(AttributeType.RELATIVE_POSITION)
    var exit = Vector2d(0.0, 50.0)

    @Attribute
    var hue: Double = 1.0

    val action = PeriodicFactory(0.05) {

        val randomHue = Rand.plusMinus(0.06f)
        val newColor = Color.createFromHSB(hue.toFloat() + randomHue, 1f, 1f)
        val newRole = ActionRole()

        val newActor = Actor(Costume(), newRole)

        with(newActor) {
            x = actor.x + exit.x
            y = actor.y + exit.y
            //zOrder = actor.zOrder - 1
            zOrder = actor.y // Useful in combination with ZThenYStageView
            changeAppearance(Resources.instance.poses.find("spark")!!)
            color = newColor
        }
        actor.stage?.add(newActor)

        val velocity = Rand.between(minVelocity, maxVelocity)
        val movement = MovePolar(newActor.position, velocity).and(DragPolar(velocity, 0.015))
        newRole.action = movement.and(Fade(newActor.color, 3.0, 0f).then(Kill(newActor)))

    }

    override fun activated() {
        action.begin()
    }

    override fun tick() {
        action.act()
    }

}
