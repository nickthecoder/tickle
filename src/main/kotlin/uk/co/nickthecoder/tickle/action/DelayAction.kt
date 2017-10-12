package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Game

class DelayAction(val seconds: Double) : Action {

    var endSeconds: Double = 0.0

    override fun begin(): Boolean {
        endSeconds = Game.instance.seconds + seconds
        return seconds <= 0.0
    }

    override fun act(): Boolean {
        return Game.instance.seconds >= endSeconds
    }
}
