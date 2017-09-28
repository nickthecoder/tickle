package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game

class Grow(val finalScale: Float, val seconds: Float) : Action {

    private var initialScale: Float = 0f

    private var startTime: Float = 0f

    override fun begin(actor: Actor) {
        initialScale = actor.scale
        startTime = Game.instance.seconds
    }

    override fun act(actor: Actor): Boolean {
        val now = Game.instance.seconds
        val t = Math.min(1.0f, (now - startTime) / seconds)

        // TODO Add an easing function

        actor.scale = initialScale * (1 - t) + finalScale * t

        return t == 1f
    }
}