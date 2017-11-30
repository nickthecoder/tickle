package uk.co.nickthecoder.tickle.resources

import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter

class SnapRotation {

    var enabled = true

    var stepDegrees = 15.0

    var closeness = 15.0

    fun snapRotation(degrees: Double): Double {
        if (!enabled) return degrees

        var mod = degrees.rem(stepDegrees)
        if (mod < 0) mod = stepDegrees + mod

        if (mod < closeness) {
            return degrees - mod
        } else {
            return degrees
        }
    }

    fun edit() {
        TaskPrompter(SnapRotationTask()).placeOnStage(Stage())

    }

    inner class SnapRotationTask() : AbstractTask() {

        val enabledP = BooleanParameter("enabled", value = enabled)

        val stepP = DoubleParameter("step", value = stepDegrees)

        val closenessP = DoubleParameter("closeness", value = closeness)

        override val taskD = TaskDescription("editGrid")
                .addParameters(enabledP, stepP, closenessP)

        override fun run() {
            enabled = enabledP.value!!
            stepDegrees = stepP.value!!
            closeness = closenessP.value!!
        }
    }

}
