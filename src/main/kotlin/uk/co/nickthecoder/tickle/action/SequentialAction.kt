package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor

class SequentialAction(vararg child: Action) : CompoundAction() {

    override val children = mutableListOf<Action>()

    var startedChild: Action? = null

    init {
        children.addAll(child)
    }

    override fun act(actor: Actor): Boolean {
        val child = children.firstOrNull()
        child ?: return true

        if (startedChild != child) {
            child.begin(actor)
            startedChild = child
        }

        if (child.act(actor)) {
            children.removeAt(0)
            return children.isEmpty()
        }
        return false
    }

    override fun then(other: Action): SequentialAction {
        children.add(other)
        return this
    }

}
