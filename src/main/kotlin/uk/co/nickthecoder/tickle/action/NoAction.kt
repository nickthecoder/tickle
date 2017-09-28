package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

/**
 * Performs no action, and never ends. It is used by ActionRole when 'die = false'.
 */
class NoAction : Action<Actor> {
    override fun act(target: Actor) = false
}
