package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor

interface Stage {

    val views : List<StageView>

    val actors: Set<Actor>

    fun begin()

    fun activated()

    fun end()

    fun tick()

    fun add(actor: Actor, activate: Boolean = true)

    fun remove(actor: Actor)

    fun addView( view : StageView)

    fun firstView() : StageView? = views.firstOrNull()
}
