package uk.co.nickthecoder.tickle.events

interface Input {

    fun isPressed(): Boolean

    fun matches(event: KeyEvent): Boolean

    companion object {

        val dummyInput = CompoundInput()

    }
}
