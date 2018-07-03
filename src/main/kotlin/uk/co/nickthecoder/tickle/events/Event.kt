package uk.co.nickthecoder.tickle.events

open class Event {

    private var consumed = false

    fun consume() {
        consumed = true
    }

    fun isConsumed() = consumed
}
