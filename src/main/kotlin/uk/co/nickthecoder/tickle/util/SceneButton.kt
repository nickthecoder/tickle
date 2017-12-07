package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent

open class SceneButton : Button() {

    @Attribute
    var scene = ""

    override fun onClicked(event: MouseEvent) {
        Game.instance.startScene(scene)
    }

}
