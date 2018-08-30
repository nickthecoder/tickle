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

interface Action {

    /**
     *  Returns true iff the action is complete, and therefore act should not be called.
     */
    fun begin(): Boolean = false

    /**
     * Returns true iff the action is complete (and should not be called again).
     */
    fun act(): Boolean

    fun then(other: Action): SequentialAction {
        return SequentialAction(this, other)
    }

    fun then(func: () -> Unit): SequentialAction {
        return then(Do(func))
    }

    fun and(other: Action): ParallelAction {
        return ParallelAction(this, other)
    }

    fun and(func: () -> Unit): ParallelAction {
        return and(Do(func))
    }

    fun forever(): ForeverAction {
        return ForeverAction(this)
    }

    fun repeat(times: Int): Repeat {
        return Repeat(this, times)
    }

    fun whilst(conditional: Action) = WhilstAction(conditional, this)

    fun until(conditional: () -> Boolean) = UntilAction(this, conditional)

    fun forSeconds(seconds: Double): UntilAction {
        val endAt = Game.instance.seconds + seconds
        return UntilAction(this) { Game.instance.seconds >= endAt }
    }

}
