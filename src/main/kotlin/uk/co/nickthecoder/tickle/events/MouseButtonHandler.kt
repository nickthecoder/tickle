package uk.co.nickthecoder.tickle.events

interface MouseButtonHandler {
    fun onMouseButton(event: MouseEvent)
}

interface MouseHandler : MouseButtonHandler {

    fun onMouseMove(event: MouseEvent)

}
