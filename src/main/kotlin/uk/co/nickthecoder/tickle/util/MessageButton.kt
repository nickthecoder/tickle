package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent

open class MessageButton : Button() {

    @Attribute
    var message = ""

    // TODO Enums not currently supported as attributes.
    //@Attribute
    var target = MessageTarget.PRODUCER

    override fun onClicked(event: MouseEvent) {
        when (target) {
            MessageTarget.PRODUCER -> Game.instance.producer.message(message)
            MessageTarget.DIRECTOR -> Game.instance.director.message(message)
        }
    }

}

enum class MessageTarget {
    PRODUCER, DIRECTOR
}
