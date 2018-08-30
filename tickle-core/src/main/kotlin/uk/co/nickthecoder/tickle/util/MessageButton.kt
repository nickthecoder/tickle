/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
