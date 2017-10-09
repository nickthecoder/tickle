package uk.co.nickthecoder.tickle.editor.util

import javafx.util.StringConverter
import org.joml.Vector2d
import uk.co.nickthecoder.paratask.parameters.CompoundParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.tickle.vector2dFromString
import uk.co.nickthecoder.tickle.vector2dToString


class Vector2dParameter(
        name: String,
        label: String = name.uncamel(),
        value: Vector2d = Vector2d(),
        description: String = "")

    : CompoundParameter<Vector2d>(
        name, label, description) {

    val xP = DoubleParameter("${name}_x", label = "X", minValue = -Double.MAX_VALUE)
    var x by xP

    val yP = DoubleParameter("${name}_y", label = "Y", minValue = -Double.MAX_VALUE)
    var y by yP

    override val converter = object : StringConverter<Vector2d>() {
        override fun fromString(string: String): Vector2d {
            return vector2dFromString(string)
        }

        override fun toString(obj: Vector2d): String {
            return vector2dToString(obj)
        }
    }

    override var value: Vector2d
        get() {
            return Vector2d(xP.value ?: 0.0, yP.value ?: 0.0)
        }
        set(value) {
            xP.value = value.x
            yP.value = value.y
        }


    init {
        this.value = value

        addParameters(xP, yP)
    }

    override fun toString(): String {
        return "Vector2dParameter : $value"
    }

    override fun copy(): Vector2dParameter {
        val copy = Vector2dParameter(name, label, value, description)
        return copy
    }
}
