package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent

open class MessageButton : Button() {

    @Attribute
    var message = ""

    /**
     * Where should the message be sent?
     * [MessageTarget.ALL] sends the message to the producer AND the director.
     */
    // TODO Enums not currently supported as attributes.
    //@Attribute
    var target = MessageTarget.ALL

    override fun onClicked(event: MouseEvent) {
        when (target) {
            MessageTarget.PRODUCER -> Game.instance.producer.message(message)
            MessageTarget.DIRECTOR -> Game.instance.director.message(message)
            MessageTarget.ALL -> {
                Game.instance.producer.message(message)
                Game.instance.director.message(message)
            }
        }
    }

}

enum class MessageTarget {
    PRODUCER, DIRECTOR, ALL
}
