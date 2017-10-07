package uk.co.nickthecoder.tickle.action

class RepeatAction(val child: Action, val times: Int) : Action {

    private var current = -1

    override fun begin(): Boolean {
        current = 1
        while (current <= times) {
            if (child.begin() == false) {
                return false
            }
            current++
        }
        return true
    }

    override fun act(): Boolean {
        if (child.act()) {
            current++
            if (current > times) {
                return true
            }
            child.begin()
        }
        return false
    }
}
