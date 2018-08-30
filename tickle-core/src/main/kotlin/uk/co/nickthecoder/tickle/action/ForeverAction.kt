package uk.co.nickthecoder.tickle.action

class ForeverAction(val child: Action) : Action {

    override fun begin(): Boolean {
        child.begin()
        return false
    }

    override fun act(): Boolean {
        if (child.act()) {
            child.begin()
        }
        return false
    }
}
