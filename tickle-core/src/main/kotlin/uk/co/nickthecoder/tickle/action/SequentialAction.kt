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
 * A compound Action, where each child action is acted upon sequentially.
 */
class SequentialAction(vararg child: Action) : CompoundAction() {

    private var childStarted = false

    /**
     * Used by ActionHolder to combine an existing Action into a sequence without restarting it.
     * It is generally not advisable for games to use this constructor directly.
     */
    constructor(child1: Action, child2: Action, isStarted: Boolean) : this(child1, child2) {
        childStarted = isStarted
    }

    override val children = mutableListOf<Action>()

    private var currentChild: Action? = null

    private var index: Int = -1

    init {
        children.addAll(child)
    }

    override fun begin(): Boolean {
        if (childStarted) {
            index = 0
            currentChild = children[0]

            // If this animation is restarted, then the normal being process should occur.
            childStarted = false

            return false
        } else {
            children.forEachIndexed { i, child ->
                if (!child.begin()) {
                    index = i
                    currentChild = child
                    return false
                }
            }
        }
        index = -1
        currentChild = null
        return true
    }

    override fun act(): Boolean {
        currentChild?.let { child ->
            if (child.act()) {
                index++
                currentChild = children.elementAtOrNull(index)
                currentChild?.let {
                    it.begin()
                    return false // Child finished, but there is another one after it
                }
                return true // Child finished, and there is none after it
            }
            return false // Child did not finish
        }
        return true // There was no current child.
    }

    /**
     * Note. Adding a new Action after this Action has already finished will NOT cause the new Action to be acted
     * upon. You may however restart the whole sequence, in which case the new Action will be acted on as usual.
     */
    override fun then(other: Action): SequentialAction {
        children.add(other)
        return this
    }

}
