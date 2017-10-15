package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Game

/**
 * Calls a [factory] function [amount] times, with [period] seconds between each.
 * If the period is less that the time between two ticks, then the factory will be called multiple times in one tick.
 *
 * If [amount] is null, then it will continue forever.
 */
class PeriodicFactory(
        val period: Double = 1.0,
        var amount: Int? = null,
        val factory: () -> Unit)

    : Action {

    var remainder: Double = 0.0

    override fun begin(): Boolean {
        remainder = 0.0
        return amount ?: 1 <= 0
    }

    override fun act(): Boolean {
        remainder += Game.instance.tickDuration
        while (remainder >= period) {
            remainder -= period
            factory()

            amount?.dec()
            if (amount == 0) {
                return true
            }
        }
        return false
    }

}
