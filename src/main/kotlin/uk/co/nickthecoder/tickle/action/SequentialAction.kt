package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class SequentialAction(vararg child: Action) : CompoundAction() {

    override val children = mutableListOf<Action>()

    var currentChild: Action? = null

    var index: Int = -1

    init {
        children.addAll(child)
    }

    override fun begin(actor: Actor) {
        index = 0
        currentChild = children.firstOrNull()
        currentChild?.begin(actor)
    }

    override fun act(actor: Actor): Boolean {
        currentChild?.let { child ->
            if (child.act(actor)) {
                index++
                currentChild = children.elementAtOrNull(index)
                currentChild?.let {
                    it.begin(actor)
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
