package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent

class QuitButton : Button() {

    override fun onMouseClicked(event: MouseEvent) {
        Game.instance.quit()
    }

}