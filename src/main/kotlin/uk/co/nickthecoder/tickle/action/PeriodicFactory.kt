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
        val amount: Int? = null,
        val factory: () -> Unit)

    : Action {

    private var created = 0

    var timeRemainder: Double = 0.0

    override fun begin(): Boolean {
        created = 0
        timeRemainder = 0.0
        return amount ?: 1 <= 0
    }

    override fun act(): Boolean {
        if (amount != null && created >= amount) {
            return true
        }
        timeRemainder += Game.instance.tickDuration
        while (timeRemainder >= period) {
            timeRemainder -= period
            factory()

            created++
            if (amount != null && created >= amount) {
                return true
            }
        }
        return false
    }

}
