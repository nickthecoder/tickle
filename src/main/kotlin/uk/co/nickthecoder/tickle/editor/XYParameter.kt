package uk.co.nickthecoder.tickle.editor

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel


class XYParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        description: String = "")

    : GroupParameter(
        name = name,
        label = label,
        description = description) {

    val xP = DoubleParameter(name + "_x", label = "X", required = required)
    var x by xP

    val commaP = InformationParameter(name + "_comma", information = ",")

    val yP = DoubleParameter(name + "_y", label = "Y", required = required)
    var y by yP

    init {
        addParameters(xP, commaP, yP)

        asHorizontal(LabelPosition.NONE)
    }

    override fun isStretchy() = false

    override fun saveChildren(): Boolean = true

    override fun errorMessage(): String? {
        return null
    }

    override fun copy(): XYParameter {
        val copy = XYParameter(name = name,
                label = label,
                description = description,
                required = required)
        copyAbstractAttributes(copy)
        return copy
    }

}
