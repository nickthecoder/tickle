package uk.co.nickthecoder.tickle.action

/**
 * Acts on both the [body] and [condition] actions together.
 * This action ends when the [condition] ends. If the [body] ends before the [condition], then this
 * will continue, but only [condition] will act.
 *
 * Note that [body] can be terminated before its natural end. [UntilAction] does not have this early
 * termination.
 */
class WhilstAction(
        val condition: Action,
        val body: Action)

    : Action {

    var thenEnded = false

    override fun begin(): Boolean {
        val result = condition.begin()
        if (!result) {
            thenEnded = body.begin()
        }
        return result
    }

    override fun act(): Boolean {
        val result = condition.act()
        if (!result && !thenEnded) {
            body.act()
        }
        return result
    }
}
