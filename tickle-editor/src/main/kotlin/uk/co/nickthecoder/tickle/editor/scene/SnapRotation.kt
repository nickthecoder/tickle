/*
Tickle
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.tickle.editor.scene

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter

class SnapRotation : HasTask {

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

    override fun task() = SnapRotationTask()


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
