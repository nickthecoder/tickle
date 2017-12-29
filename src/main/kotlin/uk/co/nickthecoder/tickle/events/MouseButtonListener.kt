package uk.co.nickthecoder.tickle.events

interface MouseButtonListener {
    fun onMouseButton(event: MouseEvent)
}

interface MouseListener : MouseButtonListener {

    fun onMouseMove(event: MouseEvent)

}
