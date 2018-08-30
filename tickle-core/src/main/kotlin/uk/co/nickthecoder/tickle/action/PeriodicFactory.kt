/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
