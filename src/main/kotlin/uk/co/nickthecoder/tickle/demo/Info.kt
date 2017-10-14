package uk.co.nickthecoder.tickle.demo

import uk.co.nickthecoder.tickle.ActionRole
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.action.Action
import uk.co.nickthecoder.tickle.action.DelayAction
import uk.co.nickthecoder.tickle.action.OneAction

class Info : ActionRole() {

    override fun createAction(): Action? =
            DelayAction(1.0)
                    .then(OneAction { actor.textAppearance?.text = text() })
                    .forever()

    fun text(): String {
        val fps = Game.instance.gameLoop.actualFPS().toInt()
        val stageInfo = Game.instance.scene.stages.map { (name, stage) ->
            "$name has ${stage.actors.count()} actors"
        }.joinToString(separator = "\n")
        return "$stageInfo\n$fps FPS"
    }
}
