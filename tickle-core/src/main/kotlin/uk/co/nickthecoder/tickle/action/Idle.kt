package uk.co.nickthecoder.tickle.action

class Idle(val ticks: Int) : Action {

    var currentTick: Int = 0

    override fun begin(): Boolean {
        currentTick = 0
        return ticks <= 0
    }

    override fun act(): Boolean {
        currentTick++
        return currentTick >= ticks
    }
}
