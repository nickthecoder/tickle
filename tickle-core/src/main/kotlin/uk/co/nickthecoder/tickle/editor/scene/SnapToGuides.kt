package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.InformationParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.tickle.editor.EditorActions
import uk.co.nickthecoder.tickle.resources.ActorResource

class SnapToGuides : SnapTo {

    var enabled: Boolean = true

    val xGuides = mutableListOf<Double>()

    val yGuides = mutableListOf<Double>()

    var closeness = 5.0

    private val adjustment = Adjustment()

    override fun snapActor(actorResource: ActorResource, adjustments: MutableList<Adjustment>) {
        if (enabled == false) return

        adjustment.reset()

        xGuides.forEach { xGuide ->
            val dx = xGuide - actorResource.x
            if (dx > -closeness && dx < closeness) {
                adjustment.x = dx
                adjustment.score = Math.abs(dx) + closeness
            }
        }

        yGuides.forEach { yGuide ->
            val dy = yGuide - actorResource.y
            if (dy > -closeness && dy < closeness) {
                adjustment.y = dy
                adjustment.score = if (adjustment.score == Double.MAX_VALUE) Math.abs(dy) + closeness else adjustment.score + Math.abs(dy)
            }
        }

        if (adjustment.score != Double.MAX_VALUE) {
            adjustments.add(adjustment)
        }
    }

    override fun task() = GuidesTask()

    override fun toString(): String {
        return "Guides enabled=$enabled closeness=$closeness x=$xGuides y=$yGuides"
    }

    inner class GuidesTask : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val toggleInfoP = InformationParameter("toggleInfo",
                information = "Note. You can toggle guide snapping using the keyboard shortcut : ${EditorActions.SNAP_TO_GUIDES_TOGGLE.shortcutLabel() ?: "<NONE>"}\n${snapInfo()}")

        val xGuidesP = MultipleParameter("xGuides", value = xGuides, isBoxed = true) {
            DoubleParameter("x")
        }

        val yGuidesP = MultipleParameter("yGuides", value = yGuides, isBoxed = true) {
            DoubleParameter("x")
        }

        val closenessP = DoubleParameter("closeness", value = closeness)

        override val taskD = TaskDescription("editGuides")
                .addParameters(enabledP, toggleInfoP, xGuidesP, yGuidesP, closenessP)

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