package uk.co.nickthecoder.tickle.editor.util

import javafx.util.StringConverter
import org.joml.Vector2f
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.vector2fFromString
import uk.co.nickthecoder.tickle.vector2fToString


class Vector2Parameter(
        name: String,
        label: String = name.uncamel(),
        value: Vector2f = Vector2f(),
        description: String = "")

    : CompoundParameter<Vector2f>(
        name, label, description) {

    val xP = DoubleParameter("${name}_x", label = "X")

    val yP = DoubleParameter("${name}_y", label = "Y")

    override val converter = object : StringConverter<Vector2f>() {
        override fun fromString(string: String): Vector2f {
            return vector2fFromString(string)
        }

        override fun toString(obj: Vector2f): String {
            return vector2fToString(obj)
        }
    }

    override var value: Vector2f
        get() {
            return Vector2f(xP.value?.toFloat() ?: 0f, yP.value?.toFloat() ?: 0f)
        }
        set(value) {
            xP.value = value.x.toDouble()
            yP.value = value.y.toDouble()
        }


    init {
        this.value = value

        addParameters(xP, yP)
        asVertical(LabelPosition.TOP)
    }

    override fun toString(): String {
        return "Polar : $value"
    }

    override fun copy(): Vector2Parameter {
        val copy = Vector2Parameter(name, label, value, description)
        return copy
    }
}
