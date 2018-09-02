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
package uk.co.nickthecoder.tickle.editor.util

import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.SimpleGroupParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.tickle.NinePatch


class NinePatchParameter(name: String, label: String)

    : SimpleGroupParameter(name, label = label) {

    val poseP = createPoseParameter("${name}_pose", label = "Pose")

    val leftP = IntParameter("${name}_left", label = "Left", value = 0)
    val bottomP = IntParameter("${name}_bottom", label = "Bottom", value = 0)
    val rightP = IntParameter("${name}_right", label = "Right", value = 0)
    val topP = IntParameter("${name}_top", label = "Top", value = 0)

    init {
        addParameters(poseP, leftP, bottomP, rightP, topP)
    }

    fun from(ninePatch: NinePatch) {
        poseP.value = ninePatch.pose

        leftP.value = ninePatch.left
        bottomP.value = ninePatch.bottom
        rightP.value = ninePatch.right
        topP.value = ninePatch.top
    }

    fun createNinePatch() = NinePatch(poseP.value!!, leftP.value!!, bottomP.value!!, rightP.value!!, bottomP.value!!)
}
