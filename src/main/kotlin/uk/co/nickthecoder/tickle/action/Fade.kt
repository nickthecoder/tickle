package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.graphics.Color

class Fade(val finalColor: Color,
           val seconds: Float) : Action {

    private lateinit var initialColor: Color

    private var startTime: Float = 0f

    override fun begin(actor: Actor) {
        initialColor = actor.color
        startTime = Game.instance.seconds
    }

    override fun act(actor: Actor): Boolean {
        val now = Game.instance.seconds
        val t = Math.min(1.0f, (now - startTime) / seconds)

        // TODO Add an easing function

        actor.color = initialColor.linearInterpolation(finalColor, t)

        return t == 1f
    }
}
