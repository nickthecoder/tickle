package uk.co.nickthecoder.tickle.editor.util

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.util.Angle


class AngleParameter(
        name: String,
        label: String = name.uncamel(),
        value: Angle = Angle(),
        description: String = "")

    : CompoundParameter<Angle>(
        name, label, description) {

    val degreesP = DoubleParameter("${name}_degrees", minValue = -360.0, maxValue = 360.0)
    var degrees by degreesP

    override val converter = object : StringConverter<Angle>() {
        override fun fromString(string: String): Angle {
            return Angle.degrees(string.toDouble())
        }

        override fun toString(obj: Angle): String {
            return obj.degrees.toString()
        }
    }

    override var value: Angle
        get() {
            return Angle.degrees(degrees ?: 0.0)
        }
        set(value) {
            degrees = value.degrees
        }


    init {
        this.value = value

        addParameters(degreesP)
        asHorizontal(LabelPosition.NONE)
    }

    override fun toString(): String {
        return "AngleParameter : $value"
    }

    override fun copy(): AngleParameter {
        val copy = AngleParameter(name, label, value, description)
        return copy
    }
}
