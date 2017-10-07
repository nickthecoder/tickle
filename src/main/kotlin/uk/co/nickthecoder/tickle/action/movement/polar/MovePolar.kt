package uk.co.nickthecoder.tickle.action.movement.polar

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.util.Heading
import uk.co.nickthecoder.tickle.util.Scalar

class MovePolar(
        val heading: Heading,
        val speed: Scalar)

    : Action<Actor> {

    override fun act(target: Actor): Boolean {
        target.position.add(heading.vector().mul(speed.value))
        return false
    }
}
