package uk.co.nickthecoder.tickle.action

/**
 * Acts on [body] while the [condition] is true. [body] is continually restarted when it ends.
 *
 * When [condition] is false, then either this WhilstBoolean ends immediately, or
 * [body] is allowed to end normally for the last time (depending on [endBodyEarly]).
 *
 * Note, [body] may never run (if [condition] is false at the start). This is in contrast to [UntilBoolean], whose
 * body runs at least once.
 */
class WhilstBoolean(
        val body: Action,
        val condition: () -> Boolean,
        val endBodyEarly: Boolean)

    : Action {

    private var bodyEnded = true
    private var conditionEnded = false

    override fun begin(): Boolean {
        conditionEnded = !condition()

        if (conditionEnded) {
            return true
        }
        bodyEnded = body.begin()
        return false
    }

    override fun act(): Boolean {
        if (!conditionEnded) {
            conditionEnded = condition()
        }

        if (conditionEnded && (bodyEnded || endBodyEarly)) {
            return true
        } else {
            bodyEnded = if (bodyEnded) body.beginAndAct() else body.act()
            return bodyEnded
        }
    }

}
