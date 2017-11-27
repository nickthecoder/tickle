package uk.co.nickthecoder.demo

import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.Do
import uk.co.nickthecoder.tickle.events.MouseEvent
import uk.co.nickthecoder.tickle.graphics.Color
import uk.co.nickthecoder.tickle.util.Button

class Info : Button() {

    val action = Delay(1.0)
            .then(Do { actor.textAppearance?.text = text() })
            .forever()

    override fun activated() {
        super.activated()
        actor.color = Color.white().semi()
        action.begin()
    }

    override fun tick() {
        action.act()
    }

    override fun onClicked(event: MouseEvent) {
        Game.instance.gameLoop.resetStats()
        actor.textAppearance?.text = text()
    }

    override fun stateChanged(down: Boolean) {
        actor.color = if (down) Color.white() else Color.white().semi()
    }

    fun text(): String {
        val fps = Math.round(Game.instance.gameLoop.actualFPS())
        val stageInfo = Game.instance.scene.stages.map { (name, stage) ->
            "$name has ${stage.actors.count()} actors"
        }.joinToString(separator = "\n")
        return "$stageInfo\n$fps FPS"
    }
}
