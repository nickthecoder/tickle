package uk.co.nickthecoder.tickle.editor.util

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2d

class PolarParameter(
        name: String,
        label: String = name.uncamel(),
        value: Polar2d = Polar2d(),
        description: String = "")

    : CompoundParameter<Polar2d>(
        name, label, description) {

    val angleP = DoubleParameter("${name}_angle", label = "Angle")
    var angle by angleP

    val magnitudeP = DoubleParameter("${name}_magnitude", label = "Magnitude")
    var magnitude by magnitudeP

    override val converter = object : StringConverter<Polar2d>() {
        override fun fromString(string: String): Polar2d {
            return Polar2d.fromString(string)
        }

        override fun toString(obj: Polar2d): String {
            return obj.toString()
        }
    }


    override var value: Polar2d
        get() {
            return Polar2d(Angle.degrees(angleP.value ?: 0.0), magnitudeP.value ?: 0.0)
        }
        set(value) {
            angleP.value = value.angle.degrees
            magnitudeP.value = value.magnitude
        }


    init {
        this.value = value

        addParameters(angleP, magnitudeP)
        asVertical(LabelPosition.TOP)
    }

    override fun toString(): String {
        return "PolarParameter : $value"
    }

    override fun copy(): PolarParameter {
        val copy = PolarParameter(name, label, value, description)
        return copy
    }
}
