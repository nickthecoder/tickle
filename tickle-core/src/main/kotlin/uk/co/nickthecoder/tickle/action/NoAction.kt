package uk.co.nickthecoder.tickle.action

/**
 * Performs no action, and never ends. It is used by ActionRole when 'die = false'.
 */
class NoAction : Action {
    override fun act() = false
}
