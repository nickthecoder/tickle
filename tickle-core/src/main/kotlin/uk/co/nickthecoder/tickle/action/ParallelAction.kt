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

import java.util.concurrent.CopyOnWriteArrayList

class ParallelAction(vararg child: Action) : CompoundAction() {

    private var firstChildStarted: Action? = null


    /**
     * Used by ActionHolder to combine an existing Action into a sequence without restarting it.
     * It is generally not advisable for games to use this constructor directly.
     */
    constructor(child1: Action, child2: Action, child1Started: Boolean) : this(child1, child2) {
        if (child1Started) firstChildStarted = child1
    }

    /**
     * This holds the children that are still active (i.e. those that haven't finished).
     * During [act], children that finish are moved from here to [finishedChildren].
     * When/if the ParallelAction is restarted, the finishedChildren are added back to
     * this list.
     */
    override val children = CopyOnWriteArrayList<Action>()

    /**
     * When adding a new child it is added to this list, their begin method is not called at this time.
     * During [act], this each member is then moved to either [finishedChildren] or [chidren]
     * depending on the result of their begin.
     */
    private val unstartedChildren = mutableListOf<Action>()

    /**
     * During [act], and [begin], any children that finish are moved from [children] into
     * this list.
     * This list is then added to [children] if/when the ParallelAction is restarted.
     */
    private val finishedChildren = mutableListOf<Action>()

    init {
        children.addAll(child)
    }

    override fun begin(): Boolean {
        super.begin()

        // If we are being restarted (from a ForeverAction, or a RepeatAction), then
        // add the finished children back to the active list
        if (finishedChildren.isNotEmpty()) {
            children.addAll(finishedChildren)
            finishedChildren.clear()
        }

        var finished = true
        children.forEach { child ->
            val childFinished = if (firstChildStarted == child) false else child.begin()
            if (childFinished) {
                children.remove(child)
                finishedChildren.add(child)
            } else {
                finished = false // One child isn't finished, so we aren't finished.
            }
        }

        // If we restart, then we don't want to treat the first child special. We need to start all children as normal.
        firstChildStarted = null

        unstartedChildren.clear()

        return finished
    }

    override fun add(action: Action) {
        unstartedChildren.add(action)
        super.add(action)
    }

    override fun remove(action: Action) {
        unstartedChildren.remove(action)
        super.add(action)
    }

    override fun act(): Boolean {

        if (unstartedChildren.isNotEmpty()) {
            unstartedChildren.forEach {
                if (it.begin()) {
                    finishedChildren.add(it)
                } else {
                    children.add(it)
                }
            }
            unstartedChildren.clear()
        }

        children.forEach { child ->
            if (child.act()) {
                children.remove(child)
                // Remember this child, so that if we are restarted, then the child can be added back to the
                // "children" list again.
                finishedChildren.add(child)
            }
        }
        return children.isEmpty()
    }

    override fun and(other: Action): ParallelAction {
        add(other)
        return this
    }

}
