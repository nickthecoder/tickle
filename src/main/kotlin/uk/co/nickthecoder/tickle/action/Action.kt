package uk.co.nickthecoder.tickle.action

interface Action<T> {

    /**
     *  Returns true iff the action is complete, and therefore act should not be called.
     */
    fun begin(target: T): Boolean = false

    /**
     * Returns true iff the action is complete (and should not be called again).
     */
    fun act(target: T): Boolean

    fun then(other: Action<T>): SequentialAction<T> {
        return SequentialAction<T>(this, other)
    }

    fun and(other: Action<T>): ParallelAction<T> {
        return ParallelAction(this, other)
    }

    fun forever(): ForeverAction<T> {
        return ForeverAction(this)
    }

}
