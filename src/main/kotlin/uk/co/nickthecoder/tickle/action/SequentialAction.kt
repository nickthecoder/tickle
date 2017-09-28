package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class SequentialAction<T>(vararg child: Action<T>) : CompoundAction<T>() {

    override val children = mutableListOf<Action<T>>()

    var currentChild: Action<T>? = null

    var index: Int = -1

    init {
        children.addAll(child)
    }

    override fun begin(target: T): Boolean {
        children.forEachIndexed { i, child ->
            if (!child.begin(target)) {
                index = i
                currentChild = child
                return false
            }
        }
        index = -1
        currentChild = null
        return true
    }

    override fun act(target: T): Boolean {
        currentChild?.let { child ->
            if (child.act(target)) {
                index++
                currentChild = children.elementAtOrNull(index)
                currentChild?.let {
                    it.begin(target)
                    return false // Child finished, but there is another one after it
                }
                return true // Child finished, and there is none after it
            }
            return false // Child did not finish
        }
        return true // There was no current child.
    }

    override fun then(other: Action<T>): SequentialAction<T> {
        children.add(other)
        return this
    }

}
