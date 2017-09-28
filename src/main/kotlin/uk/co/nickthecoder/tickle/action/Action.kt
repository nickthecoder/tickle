package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

interface Action {

    /**
     *  Returns true iff the action is complete, and therefore act should not be called.
     */
    fun begin(actor: Actor): Boolean = false

    /**
     * Returns true iff the action is complete (and should not be called again).
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
