package uk.co.nickthecoder.tickle.events

interface WindowListener : MouseButtonListener {

    fun onKey(event: KeyEvent)

    fun onResize(event: ResizeEvent)

}
