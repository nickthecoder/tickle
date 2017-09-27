package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor

interface Stage {

    val name: String

    val actors: Set<Actor>

    fun begin()

    fun end()

    fun tick()

    fun add(actor: Actor, activate: Boolean = true)

    fun remove(actor: Actor)
}
