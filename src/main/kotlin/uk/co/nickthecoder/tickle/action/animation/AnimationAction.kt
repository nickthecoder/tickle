package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action

abstract class AnimationAction<T>(
        val seconds: Float,
        val ease: Ease)

    : Action<T> {

    protected var startTime: Float = -1f

    override fun begin(target: T): Boolean {
        startTime = Game.instance.seconds
        storeInitialValue(target)
        return seconds <= 0f
    }

    override fun act(target: T): Boolean {
        val now = Game.instance.seconds
        val t = Math.min(1.0f, (now - startTime) / seconds)

        update(target, ease.ease(t))

        if (t == 1f) {
            ended()
        }
        return t == 1f
    }

    abstract protected fun storeInitialValue(target: T)

    abstract protected fun update(target: T, t: Float)

    open protected fun ended() {}

    companion object {
        fun lerp(from: Float, to: Float, t: Float) = (1 - t) * from + t * to
    }

}
