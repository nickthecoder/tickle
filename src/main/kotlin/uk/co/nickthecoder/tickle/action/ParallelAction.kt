package uk.co.nickthecoder.tickle.action

import java.util.concurrent.CopyOnWriteArrayList

class ParallelAction(vararg child: Action) : CompoundAction() {

    override val children = CopyOnWriteArrayList<Action>()

    private val unstartedChildren = mutableListOf<Action>()

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
            if (!child.begin()) {
                finished = false // One child isn't finished, so we aren't finished.
            }
        }
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
                it.begin()
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
