package uk.co.nickthecoder.tickle

import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.ParallelAction

interface Role {

    var actor: Actor

    fun begin() {}

    fun activated() {}

    fun tick()

    fun end() {}
}

abstract class AbstractRole : Role {

    override lateinit var actor: Actor

    val actions = ParallelAction()

    override fun tick() {
        actions.act(actor)
    }
}

/**
 * A Role that only has a single Actions, and does nothing in the tick method itself.
 * When all the action is complete, the actor dies.
 */
class ActionRole(val action: Action) : Role {

    override lateinit var actor: Actor

    override fun tick() {
        if (action.act(actor)) {
            actor.die()
        }
    }
}
