package uk.co.nickthecoder.tickle.action.animation

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action

abstract class AnimationAction(
        val seconds: Double,
        val ease: Ease)

    : Action {

    protected var startTime: Double = -1.0
    protected var previousT: Double = 0.0

    override fun begin(): Boolean {
        previousT = 0.0
        startTime = Game.instance.seconds
        storeInitialValue()

        if (seconds <= 0.0) {
            update(1.0)
            ended()
            return true
        }

        return false
    }

    override fun act(): Boolean {
        val now = Game.instance.seconds
        val s = Math.min(1.0, (now - startTime) / seconds)

        val t = ease.ease(s)
        update(t)
        previousT = t

        if (s == 1.0) {
            ended()
            return true
        }
        return false
    }

    fun delta(t: Double) = t - previousT

    fun interval(t: Double) = delta(t) * seconds

    fun elapsed(t: Double) = t * seconds

    abstract protected fun storeInitialValue()

    abstract protected fun update(t: Double)

    open protected fun ended() {}

    companion object {
        fun lerp(from: Double, to: Double, t: Double) = (1 - t) * from + t * to
        fun lerp(from: Float, to: Float, t: Float) = (1 - t) * from + t * to
    }

}
