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
        println("Adding nine patch parameters")
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
