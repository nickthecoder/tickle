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

    fun then(func: () -> Unit): SequentialAction {
        return then(OneAction(func))
    }

    fun and(other: Action): ParallelAction {
        return ParallelAction(this, other)
    }

    fun and(func: () -> Unit): ParallelAction {
        return and(OneAction(func))
    }

    fun forever(): ForeverAction {
        return ForeverAction(this)
    }

    fun repeat(times: Int): Repeat {
        return Repeat(this, times)
    }

}
