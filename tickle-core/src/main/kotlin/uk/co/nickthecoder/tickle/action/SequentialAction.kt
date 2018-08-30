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
