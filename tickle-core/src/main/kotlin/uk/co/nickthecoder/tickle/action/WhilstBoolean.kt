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
 * Acts on [body] while the [condition] is true. [body] is continually restarted when it ends.
 *
 * When [condition] is false, then either this WhilstBoolean ends immediately, or
 * [body] is allowed to end normally for the last time (depending on [endBodyEarly]).
 *
 * Note, [body] may never run (if [condition] is false at the start). This is in contrast to [UntilBoolean], whose
 * body runs at least once.
 */
class WhilstBoolean(
        val body: Action,
        val condition: () -> Boolean,
        val endBodyEarly: Boolean)

    : Action {

    private var bodyEnded = true
    private var conditionEnded = false

    override fun begin(): Boolean {
        conditionEnded = !condition()

        if (conditionEnded) {
            return true
        }
        bodyEnded = body.begin()
        return false
    }

    override fun act(): Boolean {
        if (!conditionEnded) {
            conditionEnded = condition()
        }

        if (conditionEnded && (bodyEnded || endBodyEarly)) {
            return true
        } else {
            bodyEnded = if (bodyEnded) body.beginAndAct() else body.act()
            return bodyEnded
        }
    }

}
