package uk.co.nickthecoder.tickle.action

/**
 * Repeatedly begins, and acts the [body] Action until the [condition] returns true.
 * This action ends when [body] ends and the [condition] returns true.
 *
 * Note that [body] will NOT terminate unnaturally early (unlike [WhilstAction]
 */
class UntilAction(
        val body: Action,
        val condition: () -> Boolean)

    : Action {

    override fun begin(): Boolean {
        body.begin()
        return false
    }

    override fun act(): Boolean {
        if (body.act()) {
            if (condition()) {
                return true
            }
            body.begin()
        }
        return false
    }
}
