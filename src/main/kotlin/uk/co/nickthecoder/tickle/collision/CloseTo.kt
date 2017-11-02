package uk.co.nickthecoder.tickle.collision

import uk.co.nickthecoder.tickle.Actor

class CloseTo(val threshold: (Actor, Actor) -> Double) : Overlapping {

    override fun overlapping(actorA: Actor, actorB: Actor): Boolean {
        val dx = actorA.x - actorB.x
        val dy = actorA.y - actorB.y
        val d2 = dx * dx + dy * dy
        return Math.sqrt(d2) < threshold(actorA, actorB)
    }
}
