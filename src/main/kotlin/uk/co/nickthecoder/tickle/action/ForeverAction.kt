package uk.co.nickthecoder.tickle.action

class ForeverAction<T>(val child: Action<T>) : Action<T> {

    override fun begin(target: T): Boolean {
        child.begin(target)
        return false
    }

    override fun act(target: T): Boolean {
        if (child.act(target)) {
            child.begin(target)
        }
        return false
    }
}
