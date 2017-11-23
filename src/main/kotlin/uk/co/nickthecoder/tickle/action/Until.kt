package uk.co.nickthecoder.tickle.action


/**
 * Performs no action, but ends when the condition is true.
 */
class Until(
        val condition: () -> Boolean)

    : Action {

    override fun begin(): Boolean {
        return condition()
    }

    override fun act(): Boolean {
        return condition()
    }
}
