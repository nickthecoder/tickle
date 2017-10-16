package uk.co.nickthecoder.tickle.action

class Repeat(val child: Action, val times: Int) : Action {

    constructor(times: Int, child: Action) : this(child, times)

    private var current = -1

    override fun begin(): Boolean {
        if (times <= 0) {
            return true
        }
        current = 1
        while (child.begin()) {
            current++
            if (current > times) {
                return true
            }
        }
        return false
    }

    override fun act(): Boolean {
        if (child.act()) {
            current++
            if (current > times) {
                return true
            }
            while (child.begin()) {
                current++
                if (current > times) {
                    return true
                }
            }
        }
        return false
    }
}
