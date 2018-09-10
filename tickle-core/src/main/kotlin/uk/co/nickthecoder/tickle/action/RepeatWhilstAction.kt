package uk.co.nickthecoder.tickle.action

/**
 * Acts on [condition] and [body] in parallel.
 * If [body] ends before [condition], then [body] is restarted (as many times as required).
 *
 * Once [condition] ends, then [body] is still acted upon, until it ends, at which time,
 * the RepeatWhilstAction ends. Therefore [condition] runs ONCE to completion, and
 * [body] runs potentially multiple times, to completion.
 * This is in contrast to [WhilstAction], whose body may end abruptly (i.e. before its natural end).
 */
class RepeatWhilstAction(
        val condition: Action,
        val body: Action)

    : Action {

    private var conditionEnded = false
    private var bodyEnded = false

    override fun begin(): Boolean {
        conditionEnded = condition.begin()
        if (!conditionEnded) {
            bodyEnded = body.begin()
        }
        return conditionEnded
    }

    override fun act(): Boolean {
        if (!conditionEnded) {
            conditionEnded = condition.act()
        }
        if (conditionEnded) {
            if (bodyEnded) {
                return true
            } else {
                bodyEnded = body.act()
                return bodyEnded
            }
        } else {
            if (bodyEnded) {
                bodyEnded = body.begin()
            } else {
                bodyEnded = body.act()
            }
            return false
        }
    }

}