package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import java.util.concurrent.CopyOnWriteArrayList

class ParallelAction<T>(vararg child: Action<T>) : CompoundAction<T>() {

    override val children = CopyOnWriteArrayList<Action<T>>()

    private val unstartedChildren = mutableListOf<Action<T>>()

    private val finishedChildren = mutableListOf<Action<T>>()

    init {
        children.addAll(child)
    }

    override fun begin(target: T): Boolean {
        super.begin(target)

        // If we are being restarted (from a ForeverAction, or a RepeatAction), then
        // add the finished children back to the active list
        if (finishedChildren.isNotEmpty()) {
            children.addAll(finishedChildren)
            finishedChildren.clear()
        }

        var finished = true
        children.forEach { child ->
            if (!child.begin(target)) {
                finished = false // One child isn't finished, so we aren't finished.
            }
        }
        unstartedChildren.clear()

        return finished
    }

    override fun add(action: Action<T>) {
        unstartedChildren.add(action)
        super.add(action)
    }

    override fun remove(action: Action<T>) {
        unstartedChildren.remove(action)
        super.add(action)
    }

    override fun act(target: T): Boolean {

        if (unstartedChildren.isNotEmpty()) {
            unstartedChildren.forEach {
                it.begin(target)
            }
            unstartedChildren.clear()
        }

        children.forEach { child ->
            if (child.act(target)) {
                children.remove(child)
                // Remember this child, so that if we are restarted, then the child can be added back to the
                // "children" list again.
                finishedChildren.add(child)
            }
        }
        return children.isEmpty()
    }

    override fun and(other: Action<T>): ParallelAction<T> {
        add(other)
        return this
    }
}
