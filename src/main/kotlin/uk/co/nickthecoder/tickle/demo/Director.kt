package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.events.KeyEvent

interface Director {

    fun begin() {}

    fun preTick() {}

    fun postTick() {}

    fun end() {}

    fun onKeyEvent(event: KeyEvent) {}

}

class NoDirector : Director {}
