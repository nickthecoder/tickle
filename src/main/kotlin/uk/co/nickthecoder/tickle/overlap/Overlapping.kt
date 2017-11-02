package uk.co.nickthecoder.tickle.overlap

import uk.co.nickthecoder.tickle.Actor

interface Overlapping {

    fun overlapping(actorA: Actor, actorB: Actor): Boolean

}
