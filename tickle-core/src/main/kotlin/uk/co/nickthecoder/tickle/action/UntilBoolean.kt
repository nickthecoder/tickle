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
 * Repeatedly begins, and acts on the [body] Action until the [condition] returns true.
 * This action ends when [body] ends and the [condition] returns true.
 *
 * [body] will be run at least once, unlike [WhilstBoolean], where the body may not run at all.
 *
 * Note that [body] will NOT terminate unnaturally early (unlike [WhilstBoolean)
 */
class UntilBoolean(
        val body: Action,
        val condition: () -> Boolean)

    : Action {

    override fun begin(): Boolean {
        body.begin()
        return false
    }

    override fun act(): Boolean {
        if (body.act()) {
            if (condition()) {
                return true
            }
            body.begin()
        }
        return false
    }
}
