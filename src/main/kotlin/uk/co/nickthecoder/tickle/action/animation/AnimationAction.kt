package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action

abstract class AnimationAction(
        val seconds: Float,
        val ease: Ease)

    : Action {

    protected var startTime: Float = -1f
    protected var previousT: Float = 0f

    override fun begin(): Boolean {
        previousT = 0f
        startTime = Game.instance.seconds
        storeInitialValue()

        if (seconds <= 0f) {
            update(1f)
            ended()
            return true
        }

        return false
    }

    override fun act(): Boolean {
        val now = Game.instance.seconds
        val s = Math.min(1.0f, (now - startTime) / seconds)

        val t = ease.ease(s)
        update(t)
        previousT = t

        if (s == 1f) {
            ended()
            return true
        }
        return false
    }

    fun delta(t: Float) = t - previousT

    fun interval(t: Float) = delta(t) * seconds

    fun elapsed(t: Float) = t * seconds

    abstract protected fun storeInitialValue()

    abstract protected fun update(t: Float)

    open protected fun ended() {}

    companion object {
        fun lerp(from: Float, to: Float, t: Float) = (1 - t) * from + t * to
        fun lerp(from: Double, to: Double, t: Float) = (1 - t) * from + t * to
    }

}
