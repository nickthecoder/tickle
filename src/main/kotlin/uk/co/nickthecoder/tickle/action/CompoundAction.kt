package uk.co.nickthecoder.tickle.action

abstract class CompoundAction<T> : Action<T> {

    protected abstract val children : MutableList<Action<T>>


    open fun add(action: Action<T>) {
        children.add(action)
    }

    open fun remove(action: Action<T>) {
        children.remove(action)
    }

}
