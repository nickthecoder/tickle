package uk.co.nickthecoder.tickle.action

abstract class CompoundAction : Action {

    abstract val children : MutableList<Action>


    open fun add(action: Action) {
        children.add(action)
    }

    fun remove(action: Action) {
        children.remove(action)
    }

}
