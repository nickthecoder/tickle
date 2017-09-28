package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Actor
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action

abstract class AnimationAction(
        val seconds: Float,
        val ease: Ease)

    : Action {

    protected var startTime: Float = -1f

    override fun begin(actor: Actor): Boolean {
        startTime = Game.instance.seconds
        storeInitialValue(actor)
        return seconds <= 0f
    }

    override fun act(actor: Actor): Boolean {
        val now = Game.instance.seconds
        val t = Math.min(1.0f, (now - startTime) / seconds)

        update(actor, ease.ease(t))

        return t == 1f
    }

    abstract protected fun storeInitialValue(actor: Actor)

    abstract protected fun update(actor: Actor, t: Float)
}
