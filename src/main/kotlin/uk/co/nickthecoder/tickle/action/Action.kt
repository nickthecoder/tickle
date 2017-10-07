package uk.co.nickthecoder.tickle.action

interface Action {

    /**
     *  Returns true iff the action is complete, and therefore act should not be called.
     */
    fun begin(): Boolean = false

    /**
     * Returns true iff the action is complete (and should not be called again).
     */
    fun act(): Boolean

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
