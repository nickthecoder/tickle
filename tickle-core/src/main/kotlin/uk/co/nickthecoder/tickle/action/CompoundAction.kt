package uk.co.nickthecoder.tickle.action

abstract class CompoundAction : Action {

    protected abstract val children : MutableList<Action>


    open fun add(action: Action) {
        children.add(action)
    }

    open fun remove(action: Action) {
        children.remove(action)
    }

}
