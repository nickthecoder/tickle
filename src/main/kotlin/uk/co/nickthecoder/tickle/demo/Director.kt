package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.events.KeyEvent
import uk.co.nickthecoder.tickle.util.TagManager

interface Director {

    val tagManager: TagManager

    fun begin() {}

    fun preTick() {}

    fun postTick() {}

    fun end() {}

    fun onKeyEvent(event: KeyEvent) {}

}

open class AbstractDirector : Director {

    override val tagManager = TagManager()

}

class NoDirector : AbstractDirector()
