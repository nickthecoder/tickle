package uk.co.nickthecoder.tickle.resources

import javafx.stage.Stage
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.asHorizontal
import uk.co.nickthecoder.tickle.editor.util.Vector2dParameter

class SnapToOthers : SnapTo {

    var enabled = true

    var closeness = Vector2d(10.0, 10.0)

    override fun snapActor(actorResource: ActorResource, adjustments: MutableList<Adjustment>) {

        if (!enabled) return

        actorResource.layer?.stageResource?.actorResources?.forEach { other ->
            if (other !== actorResource) {
                val dx = actorResource.x - other.x
                val dy = actorResource.y - other.y
                if (dx > -closeness.x && dx < closeness.x && dy > -closeness.y && dy < closeness.y) {
                    adjustments.add(Adjustment(-dx, -dy, Math.abs(dx) + Math.abs(dy)))
                }
            }
        }
    }

    override fun edit() {
        TaskPrompter(SnapToOthersTask()).placeOnStage(Stage())
    }


    inner class SnapToOthersTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val closenessP = Vector2dParameter("closeness", value = closeness, description = "Snap when this close to a grid marker")
                .asHorizontal()

        override val taskD = TaskDescription("snapToOthers")
                .addParameters(enabledP, closenessP)


        override fun run() {
            enabled = enabledP.value!!
            closeness.set(closenessP.value)
        }

    }

}
