package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

interface Action {

    fun begin(actor: Actor) {}

    /**
     * Returns true iff the action is complete (and show not be called again).
     */
    fun act(actor: Actor): Boolean

    fun then(other: Action): SequentialAction {
        return SequentialAction(this, other)
    }

    fun and(other: Action): ParallelAction {
        return ParallelAction(this, other)
    }

    fun forever(): ForeverAction {
        return ForeverAction(this)
    }
}
