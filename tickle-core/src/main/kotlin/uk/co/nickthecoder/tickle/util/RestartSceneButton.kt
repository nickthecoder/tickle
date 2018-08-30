package uk.co.nickthecoder.tickle.util

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.events.MouseEvent

open class RestartSceneButton : Button() {

    override fun onClicked(event: MouseEvent) {
        Game.instance.startScene(Game.instance.sceneName)
    }

}
