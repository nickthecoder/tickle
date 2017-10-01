package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel


class RectParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        val bottomUp: Boolean = true,
        description: String = "")

    : GroupParameter(
        name = name,
        label = label,
        description = description) {

    val leftP = IntParameter(name + "_left", label = "Left", required = required)
    var left by leftP

    val bottomP = IntParameter(name + "_bottom", label = "Bottom", required = required)
    var bottom by bottomP

    val rightP = IntParameter(name + "_right", label = "Right", required = required)
    var right by rightP

    val topP = IntParameter(name + "_top", label = "Top", required = required)
    var top by topP


    init {
        if (bottomUp) {
            addParameters(leftP, bottomP, rightP, topP)
        } else {
            addParameters(leftP, topP, rightP, bottomP)
        }
        asHorizontal(LabelPosition.TOP)
    }

    override fun saveChildren(): Boolean = true

    override fun errorMessage(): String? {
        if (leftP.value != null && rightP.value != null && leftP.value!! > rightP.value!!) {
            return "Left cannot be greater than right"
        }
        if (bottomUp) {
            if (bottomP.value != null && topP.value != null && bottomP.value!! > topP.value!!) {
                return "Bottom cannot be greater than top"
            }
        } else {
            if (bottomP.value != null && topP.value != null && topP.value!! > bottomP.value!!) {
                return "Top cannot be greater than bottom"
            }
        }
        return null
    }

    override fun copy(): RectParameter {
        val copy = RectParameter(name = name,
                label = label,
                description = description,
                bottomUp = bottomUp,
                required = required)
        copyAbstractAttributes(copy)
        return copy
    }

}
