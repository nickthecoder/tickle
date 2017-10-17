package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.Delay
import uk.co.nickthecoder.tickle.action.OneAction
import uk.co.nickthecoder.tickle.graphics.Color

class Info : ActionRole() {

    override fun createAction(): Action? {
        actor.color = Color.white().semi()
        return Delay(1.0)
                .then(OneAction { actor.textAppearance?.text = text() })
                .forever()
    }

    fun text(): String {
        val fps = Math.round(Game.instance.gameLoop.actualFPS())
        val stageInfo = Game.instance.scene.stages.map { (name, stage) ->
            "$name has ${stage.actors.count()} actors"
        }.joinToString(separator = "\n")
        return "$stageInfo\n$fps FPS"
    }
}
