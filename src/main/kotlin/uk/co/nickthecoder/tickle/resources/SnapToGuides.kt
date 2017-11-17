package uk.co.nickthecoder.tickle.resources

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter

class SnapToGuides {

    var enabled: Boolean = true

    val xGuides = mutableListOf<Double>()

    val yGuides = mutableListOf<Double>()

    var closeness = 5.0

    fun snapActor(actorResource: ActorResource): Boolean {
        if (enabled == false) return false

        var snapped = false

        xGuides.forEach { xGuide ->
            val dx = xGuide - actorResource.x
            if (dx >= 0 && dx < closeness) {
                actorResource.x += dx
                snapped = true
            } else if (dx < 0 && dx > -closeness) {
                actorResource.x -= dx
                snapped = true
            }
        }

        yGuides.forEach { yGuide ->
            val dy = yGuide - actorResource.x
            if (dy >= 0 && dy < closeness) {
                actorResource.x += dy
                snapped = true
            } else if (dy < 0 && dy > -closeness) {
                actorResource.x -= dy
                snapped = true
            }
        }

        return snapped
    }

    fun edit() {
        TaskPrompter(GuidesTask()).placeOnStage(Stage())
    }

    override fun toString(): String {
        return "Guides enabled=$enabled closeness=$closeness x=$xGuides y=$yGuides"
    }

    inner class GuidesTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val xGuidesP = MultipleParameter("xGuides", value = xGuides, isBoxed = true) {
            DoubleParameter("x")
        }

        val yGuidesP = MultipleParameter("yGuides", value = yGuides, isBoxed = true) {
            DoubleParameter("x")
        }

        val closenessP = DoubleParameter("closeness", value = closeness)

        override val taskD = TaskDescription("editGuides")
                .addParameters(enabledP, xGuidesP, yGuidesP, closenessP)

        init {
            xGuidesP.value = xGuides
        }

        override fun run() {
            xGuides.clear()
            yGuides.clear()

            xGuidesP.value.forEach { xGuides.add(it!!) }
            yGuidesP.value.forEach { yGuides.add(it!!) }

            closeness = closenessP.value!!
            enabled = enabledP.value!!
        }
    }

}
