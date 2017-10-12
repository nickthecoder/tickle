package uk.co.nickthecoder.tickle.action

class OneAction(val func: () -> Unit) : Action {

    override fun begin(): Boolean {
        func()
        return true
    }

    override fun act(): Boolean {
        return true
    }
}
