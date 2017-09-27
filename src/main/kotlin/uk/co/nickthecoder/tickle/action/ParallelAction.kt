package uk.co.nickthecoder.tickle.action

import uk.co.nickthecoder.tickle.Actor
import java.util.concurrent.CopyOnWriteArrayList

class ParallelAction(vararg child: Action) : CompoundAction() {

    override val children = CopyOnWriteArrayList<Action>()

    private var actor: Actor? = null

    init {
        children.addAll(child)
    }

    override fun begin(actor: Actor) {
        this.actor = actor
        children.forEach {
            it.begin(actor)
        }
    }

    override fun add(action: Action) {
        super.add(action)
        actor?.let {
            action.begin(it)
        }
    }

    override fun act(actor: Actor): Boolean {
        children.forEach { child ->
            if (child.act(actor)) {
                children.remove(child)
            }
        }
        return children.isEmpty()
    }

    override fun and(other: Action): ParallelAction {
        children.add(other)
        return this
    }
}
