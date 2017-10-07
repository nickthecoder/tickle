package uk.co.nickthecoder.tickle.action.movement.polar

import org.joml.Vector2f
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.util.Scalar

class MovePolar(
        val position : Vector2f,
        val heading: Heading,
        val speed: Scalar)

    : Action {

    override fun act(): Boolean {
        position.add(heading.vector().mul(speed.value))
        return false
    }
}
