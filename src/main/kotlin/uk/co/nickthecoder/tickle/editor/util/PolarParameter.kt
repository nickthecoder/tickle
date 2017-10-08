package uk.co.nickthecoder.tickle.editor.util

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.util.Angle
import uk.co.nickthecoder.tickle.util.Polar2f

class PolarParameter(
        name: String,
        label: String = name.uncamel(),
        value: Polar2f = Polar2f(),
        description: String = "")

    : CompoundParameter<Polar2f>(
        name, label, description) {

    val angleP = DoubleParameter("${name}_angle", label = "Angle")

    val magnitudeP = DoubleParameter("${name}_magnitude", label = "Magnitude")

    override val converter = object : StringConverter<Polar2f>() {
        override fun fromString(string: String): Polar2f {
            return Polar2f.fromString(string)
        }

        override fun toString(obj: Polar2f): String {
            return obj.toString()
        }
    }


    override var value: Polar2f
        get() {
            return Polar2f(Angle.degrees(angleP.value ?: 0.0), magnitudeP.value?.toFloat() ?: 0f)
        }
        set(value) {
            angleP.value = value.angle.degrees
            magnitudeP.value = value.magnitude.toDouble()
        }


    init {
        this.value = value

        addParameters(angleP, magnitudeP)
        asVertical(LabelPosition.TOP)
    }

    override fun toString(): String {
        return "Polar : $value"
    }

    override fun copy(): PolarParameter {
        val copy = PolarParameter(name, label, value, description)
        return copy
    }
}
