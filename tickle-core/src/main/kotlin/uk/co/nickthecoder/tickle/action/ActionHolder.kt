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
 * This has several advantages over using Action alone.
 * - You do not need to call [Action.begin]
 * - It is easier to combine actions using [then] and [and].
 * - You can check if the Action has finished using [isFinished], which [Action] lacks
 */
open class ActionHolder {

    private var action: Action? = null
        set(v) {
            field = v
            v?.begin()
        }

    fun act(): Boolean {
        if (action?.act() == true) {
            action = null
        }
        return isFinished()
    }

    fun isFinished() = action == null

    /**
     * Replace the current Action with a new one (the old one will no longer be acted upon).
     *
     * Note, you do not have to call [Action.begin] on newAction, ActionHolder will do that for you.
     */
    fun replaceAction(newAction: Action?) {
        action = newAction
    }

    /**
     * Perform [thenAction] sequentially with the current Action.
     * If there is no current Action, then this is the same as [replace].
     *
     * Note, you do not have to call [Action.begin] on newAction, ActionHolder will do that for you.
     */
    fun then(thenAction: Action?) {
        thenAction ?: return

        val a = action
        if (a == null) {
            action = thenAction
        } else {
            if (a is SequentialAction) {
                a.add(thenAction)
            } else {
                action = SequentialAction(a, thenAction, true)
            }
        }
    }

    fun then(func: () -> Unit) {
        then(Do(func))
    }


    /**
     * Perform [andAction] in parallel with the current Action.
     * If there is no current Action, then this is the same as [replace].
     *
     * Note, you do not have to call [Action.begin] on newAction, ActionHolder will do that for you.
     */
    fun and(andAction: Action?) {
        andAction ?: return

        val a = action
        if (a == null) {
            action = andAction
        } else {
            if (a is ParallelAction) {
                a.add(andAction)
            } else {
                action = ParallelAction(a, andAction, true)
            }
        }
    }

    fun and(func: () -> Unit) {
        and(Do(func))
    }
}