package uk.co.nickthecoder.tickle.action

/**
 * Perform the 'thenAction' while the condition action is still active.
 * Ends when the 'condition' ends.
 * If the 'thenAction' ends before the 'condition', only the 'condition' will act.
 */
class WhileAction(
        val condition: Action,
        val thenAction: Action)

    : Action {

    var thenEnded = false

    override fun begin(): Boolean {
        val result = condition.begin()
        if (!result) {
            thenEnded = thenAction.begin()
        }
        return result
    }

    override fun act(): Boolean {
        val result = condition.act()
        if (!result && !thenEnded) {
            thenAction.act()
        }
        return result
    }
}
