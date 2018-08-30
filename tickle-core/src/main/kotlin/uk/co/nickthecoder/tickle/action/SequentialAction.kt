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

import uk.co.nickthecoder.tickle.Actor

class SequentialAction(vararg child: Action) : CompoundAction() {

    override val children = mutableListOf<Action>()

    var currentChild: Action? = null

    var index: Int = -1

    init {
        children.addAll(child)
    }

    override fun begin(): Boolean {
        children.forEachIndexed { i, child ->
            if (!child.begin()) {
                index = i
                currentChild = child
                return false
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

    override fun then(other: Action): SequentialAction {
        children.add(other)
        return this
    }

}
