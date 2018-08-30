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

/**
 * Acts on both the [body] and [condition] actions together.
 * This action ends when the [condition] ends. If the [body] ends before the [condition], then this
 * will continue, but only [condition] will act.
 *
 * Note that [body] can be terminated before its natural end. [UntilAction] does not have this early
 * termination.
 */
class WhilstAction(
        val condition: Action,
        val body: Action)

    : Action {

    var thenEnded = false

    override fun begin(): Boolean {
        val result = condition.begin()
        if (!result) {
            thenEnded = body.begin()
        }
        return result
    }

    override fun act(): Boolean {
        val result = condition.act()
        if (!result && !thenEnded) {
            body.act()
        }
        return result
    }
}
