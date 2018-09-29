package uk.co.nickthecoder.tickle.physics

import org.jbox2d.dynamics.Body
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
}

fun Body.tickleBody() = (userData as TickleBody)

fun Body.actor() = tickleBody().actor
