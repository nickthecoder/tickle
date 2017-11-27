package uk.co.nickthecoder.tickle.stage

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Role

interface Stage {

    val views: List<StageView>

    val actors: Set<Actor>

    fun begin()

    fun activated()

    fun end()

    fun tick()

    fun add(actor: Actor, activate: Boolean = true)

    fun remove(actor: Actor)

    fun addView(view: StageView)

    fun firstView(): StageView? = views.firstOrNull()

}

inline fun <reified T : Role> Stage.findRole(): T? {
    return findRoles<T>().firstOrNull()
}

inline fun <reified T : Role> Stage.findRoles(): List<T> {
    return actors.filter { it.role is T }.map { it.role }.filterIsInstance<T>()
}
