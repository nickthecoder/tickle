package uk.co.nickthecoder.tickle.events

interface WindowListener : MouseButtonHandler {

    fun onKey(event: KeyEvent)

    fun onResize(event: ResizeEvent)

}
